# AdministratorApp
App for the Election Administrator, which is part of the [*MoCa QR*](http://mocaqr.niclabs.cl) Voting System project.

Android app for the administrators to run all the following tasks:
- Set the list of candidates and upload candidates.xml to the BB.
- Check integrity and correctness of the BB.
- Calculate the ballots multiplication at the end of the election and upload the value to the BB.

## Files
1. **MainActivity.java**:

2. **MultiplyBallotsActivity.java**:

3. **AuthorityPublicKey.java**:

4. **Ballot.java**:

## Minimum Requirements
### Hardware

### Apps installed

## How to Use
* Make sure you satisfy the minimum requirements described above.
* Install the .apk file, which can be downloaded from [here](https://github.com/CamiloG/moca_qr/blob/master/Admin_Apps/ballotsMultiplicationApp.apk?raw=true).

### Configuration
* First of all you have to configure the root address for the Bulletin Board server. Select 'Configure Bulletin Board address' and introduce the address.
* The address is now shown on the top box of the main window.

### Set list of candidates
*Not implemented yet.*

### Check integrity of Bulletin Board
*Not implemented yet.*

### Multiplication Process
* At the end of the election, select 'Multiply Ballots'.
* The program will download all the ballots present in the Bulletin Board server.
* Now it will multiply all of them, using also the public key stored in the Bulletin Board.
* The resulting value will be uploaded to the Bulletin Board server, finishing the application.
