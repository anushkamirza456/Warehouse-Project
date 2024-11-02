// am2789

// Imports.
import java.util.*;

/**
 * The warehouse.
 * It is divided into a rectangular grid for storing items.
 * Each item consists of a part number and a quantity
 * Only one type of part is stored in any location of the grid.
 *
 * TODO: Complete parts 1, 4, 5, 6, 7 and 8 in this class.
 */
public class Warehouse {
    // The maximum quantity in any location of the grid.
    private static final int MAX_AMOUNT = 500;
    // The number of rows and columns.
    private final int numRows, numCols;
    // The grid.
    // Empty locations must be stored as null values.
    private final Item[][] grid;

    /**
     * Create an empty warehouse of the given number of rows and columns.
     * @param numRows The number of rows.
     * @param numCols The number of columns.
     */
    public Warehouse(int numRows, int numCols){
        this.numRows = numRows;
        this.numCols = numCols;
        grid = new Item[this.numRows][this.numCols];
    }

    /**
     * TODO: Part 1a.
     * Get a list of all the locations currently storing parts.
     * @return a list of locations containing parts.
     */
    public List<Location> getPartLocations()
    {
        List<Location> partLocations = new ArrayList<>();
        // TODO: find all the locations in the warehouse that contain parts.
        // In other words, grid[aLocation.row()][aLocation.col()] is not null.
        // Store all the occupied locations in the partLocations list.
        // Iterate through columns and rows.
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col] != null) {
                    partLocations.add(new Location(row, col)); // Stored locations in the partLocations.
                }
            }
        }
        return partLocations; // Returning the list of locations with the parts.
    }

    /**
     * TODO: Part 1b
     * Get the item (if any) at the given location.
     * @param theLocation The location.
     * @return The item, or null if the location is empty.
     */
    public Item getItemAt(Location theLocation)
    {
        int rows = theLocation.row();
        int column = theLocation.col();

        // Check if the data is in bounds.
        if (rows >= 0 && rows < numRows && column >= 0 && column < numCols) {
            return grid[rows][column];
        }
        // TODO: Return the item at the given location.
        return null; // If it is out of bounds.
    }

    /**
     * TODO: Part 1c.
     * Get the total quantity of the given part.
     * @param partCode The part code.
     * @return the total quantity of the part.
     */
    public int getPartCount(int partCode)
    {
        int count = 0; // Set to 0 so data isn't altered.
        // TODO find the locations with given part code and total their quantities.

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Item item = grid[row][col]; // Get the item at the given location.
                if (item != null && item.getPartCode() == partCode) { // Check the data matches.
                    count += item.getQuantity(); // Add data to count.
                }
            }
        }

        return count;
    }

    /**
     * TODO: Part 1d.
     * Get the list of locations of the given part.
     * @param partCode The part to locate.
     * @return A list of locations; may be empty.
     */
    public List<Location> findPart(int partCode)
    {
        List<Location> locationList = new ArrayList<>();
        // TODO: Store all locations with the given part code in locationList.
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Item item = grid[row][col];
                if (item != null && item.getPartCode() == partCode && item.getQuantity() > 0) {
                    locationList.add(new Location(row, col));
                }
            }
        }
        return locationList;
    }

    // NB: TODO Part 2 is in the PartsInventory class.
    // NB: TODO Part 3 is in the DatabaseHandler class.

    /**
     * TODO: Part 4.
     * Find out whether all the items in the order are fully in stock.
     * @param order The order to check.
     * @return true if all the items are in stock, false otherwise.
     */
        // TODO: For each item in the customer's order, check if there
        // enough in the warehouse to meet the customer's needs.
    public boolean canBeFilled(ClientOrder order) {
        ItemInventory itemInventory = order.getItemInventory(); // Call the item data from the order.
        for (Item item : itemInventory.getItems()) {
            int partCode = item.getPartCode();
            // Check what is the required quantity.
            int requiredQuantity = item.getQuantity();

            int availableQuantity = getPartCount(partCode); // Get what is avaliable.
            if (availableQuantity < requiredQuantity) {
                return false; // If not avaliable, this makes sure it can be ordered in the later code.
            }
        }
        return true; // Only if everything is 100%.
    }

    /**
     * TODO: Part 5.
     * Create a purchase order to restock any items that are not in
     * the warehouse.
     * @param partsInventory The inventory of parts.
     * @return A purchase order, or null if none need restocking.
     */
    public PurchaseOrder createRestockOrder(PartsInventory partsInventory) {
        // Generates a random order purchase number.
        int orderNumber = PurchaseOrder.getNextOrderNumber();

        // Set to specific value.
        int amountRequired = 50;
        ItemInventory purchaseInventory = new ItemInventory(); // Create new inventory to store.
        PurchaseOrder thePurchaseOrder =
                new PurchaseOrder(orderNumber, Date.getNow(), purchaseInventory, false);
        // TODO: For each product/part in the parts inventory that is not in the warehouse
        // TODO: Only return the purchase order if there are items in it.
        for (Part part : partsInventory.getParts()) { // Iterate.
            int partCode = part.partCode(); // Fetch the current part.
            int existingQuantity = getPartCount(partCode); // Check the existing quality.
            if (existingQuantity == 0) { // This means it doesn't exist so it needs to be added.

                // Create a new item.
                Item restockItem = new Item(part.partCode(), amountRequired);
                purchaseInventory.addItem(restockItem); // Add restock items to purch.
            }
        }

        // Only return the purchase order if they're in it.
        if (!purchaseInventory.getItems().isEmpty()) {
            return thePurchaseOrder;
        } else {
            return null; // Return null if not.
        }
    }



    /**
     * TODO: Part 6.
     * Create a purchase order for the missing parts in the client's order.
     * @param clientOrder The client order.
     * @return The purchase order.
     */
    public PurchaseOrder createPurchaseOrder(ClientOrder clientOrder)
    {
        assert ! canBeFilled(clientOrder); // checks for errors.

        int orderNumber = PurchaseOrder.getNextOrderNumber();

        // Create a new item inventory.
        ItemInventory purchaseInventory = new ItemInventory();
        // New purchase order set to empty for now.
        PurchaseOrder thePurchaseOrder =
                new PurchaseOrder(orderNumber, Date.getNow(), purchaseInventory, false);
        // TODO: Work out which parts need to be ordered.
        // Iterate.
        for (Item item : clientOrder.getItemInventory().getItems()) {

            // Retrieve 3 int values for the part code.
            int partCode = item.getPartCode();
            int requiredQuantity = item.getQuantity();
            int availableQuantity = getPartCount(partCode);

            // If available < required, a purchase order should be created.
            if (availableQuantity < requiredQuantity) {
                // Use - to find out how much is needed.
                int additionalQuantityRequired = requiredQuantity - availableQuantity;
                // Use to create object for the specific purchase.
                Item purchaseItem = new Item(partCode, additionalQuantityRequired);

                purchaseInventory.addItem(purchaseItem); // Now this should be added to the 'purchase inventory'.
            }
        }

        return thePurchaseOrder;
    }

    /**
     * TODO: Part 7.
     * Use the order to create a pick list: a list of the locations
     * from which the parts must be taken to fulfil the order.
     * @param order The client's order.
     * @return The pick list.
     */
    public List<PickListItem> createAPickList(ClientOrder order)
    {
        List<PickListItem> pickListItems = new ArrayList<>();
        // TODO: For each item in the customer's order, find the locations
        // where the part is stored and create enough PickItems to fulfil
        // the order.

        assert locationsOk(); // This will ensure the location actually exists.

        for (Item orderItem : order.getItemInventory().getItems()) {
            // Fetching the quanitity required.
            int requiredQuantity = orderItem.getQuantity();
            int partCode = orderItem.getPartCode();
            List<Location> partLocations = findPart(partCode);

            // Iterate over the location.
            for (Location location : partLocations) {
                Item itemAtLocation = getItemAt(location);
                int availableQuantity = itemAtLocation.getQuantity(); // Create items that are enough for the order.

                // Check if there is enough items at that location.
                if (availableQuantity >= requiredQuantity) {

                    //If yes then create PickList and add it.
                    pickListItems.add(new PickListItem(location, new Item(partCode, requiredQuantity)));
                    itemAtLocation.reduceQuantity(requiredQuantity);

                    break; // Stop iterating.

                }
                else {
                    // If there are not the required number of items create PickList.
                    pickListItems.add(new PickListItem(location, new Item(partCode, availableQuantity)));
                    requiredQuantity -= availableQuantity; // Reduced - Avaliable to find total.
                    itemAtLocation.reduceQuantity(availableQuantity); // Now reduce to 0.
                }
            }
        }
        // assert locationsOk();
        return pickListItems;


    }

    /**
     * TODO: Part 8.
     * Store the items in the delivery.
     * Where an item is already present in the warehouse, add it to the
     * existing locations up to the MAX_AMOUNT in a location.
     * Where an item is not present - or all existing locations of the
     * item are already full, store it in any empty location.
     * At the end of this method, no location may contain more than
     * MAX_AMOUNT of any item.
     * @param theDelivery The delivery to be distributed in the warehouse.
     * @return A list of where the items were stored.
     */
    // Part 8 complete.
    public List<Location> storeDelivery(Delivery theDelivery)
    {
        List<Location> whereStored = new ArrayList<>();
        for(Item theItem : theDelivery.getItemInventory().getItems()) {
            // TODO:
            //  Where an item is already present in the warehouse,
            //  add the quantity to the existing locations up to the
            //  MAX_AMOUNT in any one location.
            //
            //  Where an item is not present - or all existing locations of the
            //  item are already full, store it in any empty location.
            // Add to the whereStored list the location(s) each item is stored in.

           // Retrieve the code and quantity for item.
            int partCode = theItem.getPartCode();
            int quantity = theItem.getQuantity();

            // Check to see if item is in the WAREHOUSE.
            List<Location> partLocations = findPart(partCode);

            boolean itemStored = false; // tracks if item is all sorted.

            // Sees if space avaliable in different defined locations.
            for (Location location : partLocations) {
                Item itemAtLocation = getItemAt(location);
                // Use to calculate total space as per requirment.
                int availableSpace = MAX_AMOUNT - itemAtLocation.getQuantity();

                if (availableSpace > 0) { // If yes, store here.
                    // Determining quantity.
                    int quantityToAdd = Math.min(availableSpace, quantity);

                    itemAtLocation.increaseQuantity(quantityToAdd);
                    quantity -= quantityToAdd; // Update it.
                    // Add location to the updated list.
                    whereStored.add(location);
                    // break;

                    if (quantity == 0) { // If fully stored, end loop and boolean.
                        itemStored = true;

                        break;
                    }
                }
            }

            // Store remaining in empty locations:
            while (quantity > 0) {
                List<Location> emptyLocations = getEmptyLocations();

                if (emptyLocations.isEmpty()) { // If all full, break.

                    break;
                }

                // Get first empty location avaliable.
                Location emptyLocation = emptyLocations.remove(0);
                Item newItem = new Item(partCode, Math.min(quantity, MAX_AMOUNT));
                // Add new item to the WAREHOUSE.
                addToWarehouse(emptyLocation, newItem);
                whereStored.add(emptyLocation);
                quantity -= newItem.getQuantity(); // Update.
            }

            // If it still won't fit, it means WAREHOUSE is all full.
            if (quantity > 0) {
                System.out.println("Error, no space in warehouse for the item:" + " " + theItem + ".");
            }
        }

        // So MAX_AMOUNT is kept in bounds.
        assert locationsOk();

        return whereStored;
    }

    /**
     * Get a list of available part codes.
     * @return the list of codes.
     */
    public List<Integer> getAvailablePartCodes()
    {
        Set<Integer> partCodes = new HashSet<>();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item item = theRow[col];
                if (item != null && item.getQuantity() != 0) {
                    partCodes.add(item.getPartCode());
                }
            }
        }
        return new ArrayList<>(partCodes);
    }

    /**
     * Add the given item to the warehouse.
     * If there is already an item there then the part codes
     * must be identical.
     * @param theLocation Where to store the item.
     * @param anItem The item to be stored.
     */
    public void addToWarehouse(Location theLocation, Item anItem) {
        Item currentItem = grid[theLocation.row()][theLocation.col()];
        assert anItem.getQuantity() > 0;

        if(currentItem == null) {
            grid[theLocation.row()][theLocation.col()] = anItem;
        }
        else {
            assert currentItem.getPartCode() == anItem.getPartCode() :
                    "Attempt to store an item where a different type is stored";
            currentItem.increaseQuantity(anItem.getQuantity());
        }

        assert grid[theLocation.row()][theLocation.col()] != null;
        assert grid[theLocation.row()][theLocation.col()].getQuantity() <= MAX_AMOUNT;
    }

    /**
     * Print the occupied locations in the warehouse.
     */
    public void printOccupiedLocations() {
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item item = theRow[col];
                if(item != null) {
                    System.out.printf("%d,%d: %s%n", row, col, item);
                }
            }
        }
    }

    /**
     * Print a map of the warehouse.
     */
    public void printMap() {
        System.out.println("Warehouse contents in a 2D visual format:");

        // Print the column numbers first
        System.out.print("    |");
        for (int col = 0; col < numCols; col++) {
            System.out.printf(" %2d|", col);
        }
        System.out.println();
        for (int w = 0; w < numCols * 4 + 5; w++) {
            System.out.print("-");
        }
        System.out.println();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];

            System.out.printf("  %2d|", row);
            for (int col = 0; col < numCols; col++) {
                if(theRow[col] != null) {
                    System.out.printf("%3d", theRow[col].getQuantity());
                }
                else {
                    System.out.print("   ");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Check that no location has more than the MAX_AMOUNT of parts
     * or zero parts.
     * @return true if everything is ok, false otherwise.
     */
    public boolean locationsOk()
    {
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item anItem = theRow[col];
                if(anItem != null &&
                        anItem.getQuantity() > MAX_AMOUNT &&
                        anItem.getQuantity() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the number of parts at the given location.
     * @param theLocation the location.
     * @return The number of parts.
     */
    private int getQuantityAt(Location theLocation)
    {
        Item theItem = grid[theLocation.row()][theLocation.col()];
        if(theItem != null) {
            return theItem.getQuantity();
        }
        else {
            return 0;
        }
    }

    /**
     * Get a randomised list of all the empty locations.
     * @return a list of empty locations.
     */
    private List<Location> getEmptyLocations()
    {
        List<Location> emptyLocations = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item anItem = theRow[col];
                if(anItem == null) {
                    emptyLocations.add(new Location(row, col));
                }
            }
        }
        Collections.shuffle(emptyLocations);
        return emptyLocations;
    }

}
