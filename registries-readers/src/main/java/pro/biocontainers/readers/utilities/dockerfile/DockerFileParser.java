package pro.biocontainers.readers.utilities.dockerfile;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ypriverol
 */
public class DockerFileParser {

    public static final Pattern LABEL_AGAIN_MULTILINE = Pattern.compile(".*(\\\\)+\\s*");
    public static final Pattern LABEL_END_MULTILINE = Pattern.compile("[^\\\\]*");

    /** Parse content of Dockerfile from the specified File. */
    public static DockerFile parse(File file) throws FileException {
        try {
            return parse(readLines(Files.newReader(file, Charset.defaultCharset())));
        } catch (IOException e) {
            throw new FileException("Error happened parsing the Docker file." + e.getMessage(), e);
        }
    }

    private static Iterable<String> readLines(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder sb = new StringBuilder();

        while(line != null){
            if (LABEL_AGAIN_MULTILINE.matcher(line).matches()) {
                sb.append(line);
            }else{
                sb.append(line).append("\n");
            }
            line = bufferedReader.readLine();
        }


        String fileAsString = sb.toString();
        System.out.println("Contents : " + fileAsString);
        return Arrays.asList(fileAsString.split("\n"));
    }

    private static DockerFile parse(Iterable<String> lines) throws FileException {
        final DockerFile dockerfile = new DockerFile();
        DockerImage current = null;
        for (String line : lines) {
            line = line.trim();
            dockerfile.getLines().add(line);
            if (!line.isEmpty()) {
                Instruction instruction;
                if ((instruction = getInstruction(line)) == null) {
                    continue;
                }

                if (instruction == Instruction.FROM) {
                    if (current != null) {
                        dockerfile.getImages().add(current);
                    }
                    current = new DockerImage();
                    instruction.setInstructionArgumentsToModel(current, line);
                } else {
                    if (current == null) {
                        if (instruction != Instruction.COMMENT) {
                            throw new FileException("Error happened parsing the Docker file: Docker file must start with 'FROM' instruction");
                        }
                    } else {
                        instruction.setInstructionArgumentsToModel(current, line);
                    }
                }
            }
        }
        if (current != null) {
            dockerfile.getImages().add(current);
        }
        return dockerfile;
    }

    private static Instruction getInstruction(String line) {
        if (line.startsWith("#")) {
            return Instruction.COMMENT;
        }
        // By convention instruction should be UPPERCASE but it is not required.
        final String lowercase = line.toLowerCase();
        if (lowercase.startsWith("from")) {
            return Instruction.FROM;
        } else if (lowercase.startsWith("maintainer")) {
            return Instruction.MAINTAINER;
        } else if (lowercase.startsWith("run")) {
            return Instruction.RUN;
        } else if (lowercase.startsWith("cmd")) {
            return Instruction.CMD;
        } else if (lowercase.startsWith("expose")) {
            return Instruction.EXPOSE;
        } else if (lowercase.startsWith("env")) {
            return Instruction.ENV;
        } else if (lowercase.startsWith("add")) {
            return Instruction.ADD;
        } else if (lowercase.startsWith("entrypoint")) {
            return Instruction.ENTRYPOINT;
        } else if (lowercase.startsWith("volume")) {
            return Instruction.VOLUME;
        } else if (lowercase.startsWith("user")) {
            return Instruction.USER;
        } else if (lowercase.startsWith("workdir")) {
            return Instruction.WORKDIR;
        } else if (lowercase.startsWith("onbuild")) {
            return Instruction.ONBUILD;
        } else if (lowercase.startsWith("label")){
            return Instruction.LABEL;
        }
        return null;
    }

    private enum Instruction {
        FROM {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.setFrom(getInstructionArguments(line));
            }
        },
        MAINTAINER {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.getMaintainer().add(getInstructionArguments(line));
            }
        },
        RUN {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.getRun().add(getInstructionArguments(line));
            }
        },
        CMD {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.setCmd(getInstructionArguments(line));
            }
        },
        EXPOSE {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                final String args = getInstructionArguments(line);
                final int l = args.length();
                int i = 0, j = 0;
                while (j < l) {
                    while (j < l && !Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    model.getExpose().add(args.substring(i, j));
                    i = j;
                    while (i < l && Character.isWhitespace(args.charAt(i))) {
                        i++;
                    }
                    j = i;
                }
            }
        },
        ENV {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                final String args = getInstructionArguments(line);
                final int l = args.length();
                int i = 0;
                while (i < l && !Character.isWhitespace(args.charAt(i))) {
                    i++;
                }
                if (i < l) {
                    int j = i;
                    while (j < l && Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    if (j < l) {
                        model.getEnv().put(args.substring(0, i), args.substring(j));
                    } else {
                        model.getEnv().put(args.substring(0, i), null);
                    }
                } else {
                    model.getEnv().put(args, null);
                }
            }
        },
        ADD {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                final String args = getInstructionArguments(line);
                final int l = args.length();
                int i = 0;
                while (i < l && !Character.isWhitespace(args.charAt(i))) {
                    i++;
                }
                if (i < l) {
                    int j = i;
                    while (j < l && Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    if (j < l) {
                        model.getAdd().add(Pair.of(args.substring(0, i), args.substring(j)));
                    } else {
                        // respect this even it's not legal for docker file
                        model.getAdd().add(Pair.of(args.substring(0, i), (String)null));
                    }
                } else {
                    // respect this even it's not legal for docker file
                    model.getAdd().add(Pair.of(args, (String)null));
                }
            }
        },
        ENTRYPOINT {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.setEntrypoint(getInstructionArguments(line));
            }
        },
        VOLUME {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                String args = getInstructionArguments(line);
                if (!args.isEmpty()) {
                    final int l = args.length();
                    if (args.charAt(0) != '[' || args.charAt(l - 1) != ']') {
                        throw new FileException(String.format("Error happened parsing the Docker file: Invalid argument '%s' for 'VOLUME' instruction", args));
                    }
                    int i = 1, j = 1, end = l - 1;
                    while (j < end) {
                        while (j < end && args.charAt(j) != ',') {
                            j++;
                        }
                        String volume = args.substring(i, j);
                        if (!volume.isEmpty()) {
                            if ((volume.charAt(0) == '"' && volume.charAt(volume.length() - 1) == '"')
                                    || (volume.charAt(0) == '\'' && volume.charAt(volume.length() - 1) == '\'')) {
                                volume = volume.substring(1, volume.length() - 1);
                            }
                            if (!volume.isEmpty()) {
                                model.getVolume().add(volume);
                            }
                        }
                        i = j + 1;
                        while (i < end && Character.isWhitespace(args.charAt(i))) {
                            i++;
                        }
                        j = i;
                    }
                }
            }
        },
        USER {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.setUser(getInstructionArguments(line));
            }
        },
        WORKDIR {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.setWorkdir(getInstructionArguments(line));
            }
        },
        COMMENT {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.getComments().add(getInstructionArguments(line));
            }

            @Override
            String getInstructionArguments(String line) {
                return line.substring(1).trim();
            }
        },
        LABEL {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.getLabels().add(getInstructionArguments(line));
            }

            @Override
            String getInstructionArguments(String line) {
                return line.substring(1).trim();
            }
        },
        ONBUILD {
            @Override
            void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException {
                model.getOnbuild().add(line.substring(name().length()).trim());
            }
        };

        abstract void setInstructionArgumentsToModel(DockerImage model, String line) throws FileException;

        String getInstructionArguments(String line) {
            return line.substring(name().length()).trim();
        }
    }

    private DockerFileParser() {
    }
}