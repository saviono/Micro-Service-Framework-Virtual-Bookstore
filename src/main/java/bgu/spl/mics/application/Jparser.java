package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class Jparser {
    public BookInventoryInfo[] initialInventory;
    public Resource[] initialResources;
    public Services services;

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public Resource[] getInitialResources() {
        return initialResources;
    }

    public Services getServices() {
        return services;
    }
}
