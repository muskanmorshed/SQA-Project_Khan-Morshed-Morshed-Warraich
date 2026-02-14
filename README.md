# Banking System 

## Overview 
The Banking System is a console-based application that supports core banking operations. It consists of:

- A Front End that handles transaction processing through a menu-based interface.
- A Back End batch processor.

The current implementation includes a complete requirements-based test suite and an initial prototype of the Front End. The system operates entirely through command-line menus and fixed-format text files.

## Front End

### Description
The Front End provides a menu-based console interface that allows users to interact with the banking system. Through this interface, users can perform core banking operations and manage accounts according to their session type.

**Features include:**
- Standard and admin login sessions
- Transactions: withdrawal, transfer, paybill, deposit
- Admin-only operations: create, delete, disable, change plan
- Session rules, transaction limits, and account constraints
- Deposits are recorded during the session and applied to account balances upon logout.
- Error messages and confirmations for invalid or successful operations

**Testing:** 
The requirements-based test suite verifies session behavior and the handling of invalid inputs or constraint violations. 

## How to Run
You can run the banking system using your IDE or from the command line.

**Using the IDE**
1. After cloning the project or downloading the files, open the project in your prefered IDE.
2. Navigate to the `Main.java` file, and press the Run button to start the program.
4. If that doesn’t run correctly, press Run with Debug to launch it in debug mode.

**Using Command Line** 
1. Open a terminal and navigate to the project directory.
2. Compile the java files
   ```bash
   javac *.java
   ```
3. Run the program with the required input files
   ```bash
   java Main accounts.txt rentals.txt transactions.txt
   ```

## Limitations
- This version is a rapid prototype and has not yet been fully validated against all test cases.
- The Back End batch processor is not yet implemented.
- Error handling may be improved in later iterations.
- Some edge cases may require refinement.

## Future Work
- Full integration with the Back End batch processor.
- Continuous refactoring and simplification.
- Full validation against all constraints.

## Authors 
- Malasa Khan
- Mehreen Morshed
- Muskan Morshed
- Shimza Warraich

