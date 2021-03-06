package pro.biocontainers.mongodb.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import pro.biocontainers.mongodb.model.BioContainer;
import pro.biocontainers.mongodb.repository.BioContainersRepository;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class BioContainersService {

    @Autowired
    BioContainersRepository repository;

    /**
     * This method index a new Container using as import the container object.
     *
     * @param container {@link BioContainer}
     * @return Optional
     */
    public Optional<BioContainer> indexContainer(BioContainer container){
        try {
            container = repository.save(container);
        }catch(DuplicateKeyException ex){
           log.error("A BioContainer with similar accession already exists!!!");
        }
        return Optional.of(container);
    }

    /**
     * Find all Containers
     * @return
     */
    public List<BioContainer> findAll(){
        return repository.findAll();
    }

    public void updateContainer(BioContainer bioContainer) {
        try {
            if(bioContainer.getId() != null){
                repository.save(bioContainer);
                log.info("The following container has been updated -- " + bioContainer.getId().toString());
            }
        }catch(DuplicateKeyException ex){
            log.error("A BioContainer with similar accession already exists!!!");
        }
    }
}
