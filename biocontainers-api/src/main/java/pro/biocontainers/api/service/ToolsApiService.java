package pro.biocontainers.api.service;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyCreator;
import org.springframework.stereotype.Service;
import pro.biocontainers.api.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToolsApiService {


    private final DummyCreator dummyCreator = new DummyCreator(ClassBindings.defaultBindings());

    /**
     * List all tools.
     *
     * @param id           A unique identifier of the tool, scoped to this registry, for example `123456`
     * @param registry     The image registry that contains the image.
     * @param organization The organization in the registry that published the image.
     * @param name         The name of the image.
     * @param toolname     The name of the tool.
     * @param description  The description of the tool.
     * @param author       The author of the tool.
     * @return An array of Tools that match the filter.
     */
    public List<Tool> get(String id, String registry, String organization, String name,
                          String toolname, String description, String author) {

        ArrayList<Tool> tools = new ArrayList<>();
        tools.add(dummyCreator.create(Tool.class));
        tools.add(dummyCreator.create(Tool.class));
        tools.add(dummyCreator.create(Tool.class));
        return tools;
    }

    /**
     * List one specific tool, acts as an anchor for self references.
     *
     * @param id A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @return A tool.
     */

    public Tool getById(String id) {

        return dummyCreator.create(Tool.class);
    }

    /**
     * Returns all versions of the specified tool.
     *
     * @param id A unique identifier of the tool, scoped to this registry.
     * @return An array of tool versions.
     */

    public List<ToolVersion> getVersions(String id) {

        ArrayList<ToolVersion> toolVersions = new ArrayList<>();
        toolVersions.add(dummyCreator.create(ToolVersion.class));
        toolVersions.add(dummyCreator.create(ToolVersion.class));
        toolVersions.add(dummyCreator.create(ToolVersion.class));
        return toolVersions;
    }

    /**
     * This endpoint returns one specific tool version.
     *
     * @param id        A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId An identifier of the tool version for this particular tool registry for example `v1`.
     * @return A tool version.
     */
    public ToolVersion getByVersionId(String id, String versionId) {

        return dummyCreator.create(ToolVersion.class);
    }

    /**
     * Returns the container specifications(s) for the specified image.
     * For example, a CWL CommandlineTool can be associated with one specification
     * for a container, a CWL Workflow can be associated with multiple specifications for containers.
     *
     * @param id        A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId An identifier of the tool version for this particular tool registry for example `v1`.
     * @return An array of the tool containerfile.
     */
    public List<ToolContainerfile> getContainerfile(String id, String versionId) {

        ArrayList<ToolContainerfile> toolContainerfiles = new ArrayList<>();
        toolContainerfiles.add(dummyCreator.create(ToolContainerfile.class));
        toolContainerfiles.add(dummyCreator.create(ToolContainerfile.class));
        toolContainerfiles.add(dummyCreator.create(ToolContainerfile.class));
        return toolContainerfiles;

    }

    /**
     * Returns the descriptor for the specified tool (examples include CWL, WDL, or Nextflow documents).
     *
     * @param type      The output type of the descriptor. If not specified, it is up to the
     *                  underlying implementation to determine which output type to return.
     *                  Plain types return the bare descriptor while the "non-plain" types
     *                  return a descriptor wrapped with metadata. Allowable values include
     *                  "CWL", "WDL", "NFL", "PLAIN_CWL", "PLAIN_WDL", "PLAIN_NFL".
     * @param id        A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId An identifier of the tool version for this particular tool registry for example `v1`.
     * @return The tool descriptor.
     */
    public ToolDescriptor getDescriptor(String type, String id, String versionId) {

        return dummyCreator.create(ToolDescriptor.class);

    }


    /**
     * Descriptors can often include imports that refer to additional
     * descriptors. This returns additional descriptors for the specified tool
     * in the same or other directories that can be reached as a relative path.
     * This endpoint can be useful for workflow engine implementations like
     * cwltool to programmatically download all the descriptors for a tool and run it.
     *
     * @param type         The output type of the descriptor. If not specified, it is up to the
     *                     underlying implementation to determine which output type to return.
     *                     Plain types return the bare descriptor while the "non-plain" types
     *                     return a descriptor wrapped with metadata. Allowable values include
     *                     "CWL", "WDL", "NFL", "PLAIN_CWL", "PLAIN_WDL", "PLAIN_NFL".
     * @param id           A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId    An identifier of the tool version for this particular tool registry for example `v1`.
     * @param relativePath A relative path to the additional file (same directory or
     *                     subdirectories), for example 'foo.cwl' would return a 'foo.cwl' from
     *                     the same directory as the main descriptor. 'nestedDirectory/foo.cwl'
     *                     would return the file  from a nested subdirectory.  Unencoded paths
     *                     such 'sampleDirectory/foo.cwl' should also be allowed.
     * @return The tool descriptor.
     */
    public ToolDescriptor getDescriptorByRelativePath(String type, String id,
                                                      String versionId, String relativePath) {

        return dummyCreator.create(ToolDescriptor.class);

    }

    /**
     * Get a list of objects that contain the relative path and file type.
     *
     * @param type      The output type of the descriptor. Examples of allowable values are
     *                  "CWL", "WDL", and "NextFlow.".
     * @param id        A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId An identifier of the tool version for this particular tool registry for example `v1`.
     * @return The array of File JSON responses.
     */
    public List<ToolFile> getFiles(String type, String id, String versionId) {

        ArrayList<ToolFile> toolfiles = new ArrayList<>();
        toolfiles.add(dummyCreator.create(ToolFile.class));
        toolfiles.add(dummyCreator.create(ToolFile.class));
        toolfiles.add(dummyCreator.create(ToolFile.class));
        return toolfiles;

    }


    /**
     * Get a list of test JSONs (these allow you to execute the tool successfully)
     * suitable for use with this descriptor type.
     *
     * @param type      The type of the underlying descriptor. Allowable values include
     *                  "CWL", "WDL", "NFL", "PLAIN_CWL", "PLAIN_WDL", "PLAIN_NFL". For
     *                  example, "CWL" would return an list of ToolTests objects while
     *                  "PLAIN_CWL" would return a bare JSON list with the content of the tests.
     * @param id        A unique identifier of the tool, scoped to this registry, for example `123456`.
     * @param versionId An identifier of the tool version for this particular tool registry for example `v1`.
     * @return The tool test JSON response.
     */
    public List<ToolTests> getTests(String type, String id, String versionId) {

        ArrayList<ToolTests> toolTests = new ArrayList<>();
        toolTests.add(dummyCreator.create(ToolTests.class));
        toolTests.add(dummyCreator.create(ToolTests.class));
        toolTests.add(dummyCreator.create(ToolTests.class));
        return toolTests;

    }
}
