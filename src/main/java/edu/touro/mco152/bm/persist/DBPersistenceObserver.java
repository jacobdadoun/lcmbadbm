package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.BMObserver;
import edu.touro.mco152.bm.command.BMCommandCenter;

import javax.persistence.EntityManager;

/**
 * DBPersistenceObserver is an observer class that will update BM info to the EM "DataBase"
 */

public class DBPersistenceObserver implements BMObserver {

    DiskRun diskRun;
    private Boolean hasBeenUpdated;

    public DBPersistenceObserver(BMCommandCenter command){
        hasBeenUpdated = false;
        this.diskRun = command.getDiskRun();
        command.registerObserver(this);
    }

    @Override
    public void update() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(diskRun);
        em.getTransaction().commit();
        hasBeenUpdated = true;
    }

    @Override
    public Boolean isUpdated() {
        return hasBeenUpdated;
    }

}
