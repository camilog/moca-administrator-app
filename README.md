# BallotsMultiplicationApp
Sixth part of the [*MoCa QR*](http://www.github.com/CamiloG/moca_qr) Voting System project.

Android app for any person to multiplicate the encrypted ballots present on the Bulletin Board server and upload the resulting value.

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

### Multiplication Process
* At the end of the election, select 'Multiply Ballots'.
* The program will download all the ballots present in the Bulletin Board server.
* Now it will multiply all of them, using also the public key stored in the Bulletin Board.
* The resulting value will be uploaded to the Bulletin Board server, finishing the application.
