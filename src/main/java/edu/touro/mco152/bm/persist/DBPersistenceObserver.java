package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.BMObserver;

import javax.persistence.EntityManager;

/**
 * DBPersistenceObserver is an observer class that will update BM info to the EM "DataBase"
 */

public class DBPersistenceObserver implements BMObserver {

    DiskRun diskRun;
    public boolean checkRunFlag;

    public DBPersistenceObserver(DiskRun diskRun){
        this.diskRun = diskRun;
        checkRunFlag = false;
    }

    @Override
    public void update() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(diskRun);
        em.getTransaction().commit();
        checkRunFlag = true;
    }

    @Override
    public boolean getCheckRunFlag() {
        return checkRunFlag;
    }
}
