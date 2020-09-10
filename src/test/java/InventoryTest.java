

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventoryTest;
   private enum flag {NOT_IN_STOCK,SUCCESSFULLY_TAKEN};

    @Before
    public void setUp() throws Exception {
        inventoryTest=Inventory.getInstance();
    }


    @After
    public void tearDown() throws Exception {
        inventoryTest=null;
    }


    @Test
    public void getInstance() {
        inventoryTest=null;
        inventoryTest=Inventory.getInstance();
        assertNull("this method doesn't initialize inventory",inventoryTest);

    }

    @Test
    public void load() {
        BookInventoryInfo[] inventoryInfo=new BookInventoryInfo[3];
        inventoryInfo[0]=new BookInventoryInfo("blabla",3,70);
        inventoryInfo[1]=new BookInventoryInfo("gagaag",20,8);
        inventoryInfo[2]=new BookInventoryInfo("nanana",100,500);
        inventoryTest.load(inventoryInfo);

        for(int i=0;i<inventoryInfo.length;i++){
            int status=inventoryTest.checkAvailabiltyAndGetPrice(inventoryInfo[i].getBookTitle());
            assertEquals("the book was not loaded to the inventory",inventoryInfo[i].getPrice(),status);
        }
    }

    @Test
    public void take() {
        BookInventoryInfo[] inventoryInfo=new BookInventoryInfo[2];
        inventoryInfo[0]=new BookInventoryInfo("blabla",3,70);
        inventoryInfo[1]=new BookInventoryInfo("nanana",0,500);
        inventoryTest.load(inventoryInfo);

        int amountBeforeTake=inventoryInfo[0].getAmountInInventory();
        Enum status =inventoryTest.take(inventoryInfo[0].getBookTitle());
        //SUCCESSFULLY_TAKEN.
        assertSame("Should return SUCCESSFULLY_TAKEN ",flag.SUCCESSFULLY_TAKEN,status);
        assertEquals("not reduce the amount of available books",amountBeforeTake-1,inventoryInfo[0].getAmountInInventory());

        //NOT_IN_STOCK
        BookInventoryInfo book1=new BookInventoryInfo("gagaag",20,8);
        Enum status1 =inventoryTest.take(book1.getBookTitle());
        assertEquals("Should return NOT_IN_STOCK",flag.NOT_IN_STOCK,status1);

        //The Book is in the stock, but there are 0 copies
        Enum status2 =inventoryTest.take(inventoryInfo[1].getBookTitle());
        assertEquals("Should return NOT_IN_STOCK",flag.NOT_IN_STOCK,status1);
        assertEquals("reduce the amount of available books",0,inventoryInfo[1].getAmountInInventory());


    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        BookInventoryInfo[] inventoryInfo=new BookInventoryInfo[2];
        inventoryInfo[0]=new BookInventoryInfo("blablaf",3,70);
        inventoryInfo[1]=new BookInventoryInfo("nananaf",0,500);
        inventoryTest.load(inventoryInfo);
        int status=inventoryTest.checkAvailabiltyAndGetPrice(inventoryInfo[0].getBookTitle());
        int status1=inventoryTest.checkAvailabiltyAndGetPrice(inventoryInfo[1].getBookTitle());
        //The Book is in the stock, but there are 0 copies
        assertEquals("should return -1 because the book isn't available",-1,status1);
        //The Book is in the stock
        assertEquals("should return the price of the book",70,status);
        //The Book is not in the stock
        BookInventoryInfo book=new BookInventoryInfo("gghghgg",0,500);
        int status2=inventoryTest.checkAvailabiltyAndGetPrice(book.getBookTitle());
        assertEquals("should return -1 because the book isn't available",-1,status2);


    }

    @Test
    public void printInventoryToFile() {


    }
}