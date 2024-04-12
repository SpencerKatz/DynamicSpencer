# DynamicSpencer

* This is my crack at the Dynamic Backend Coding Interview. I use the Web3j library in my implementation.

## Running
* Run the Program from the Main class.

## Features
- **Account Creation**: Users can log in or sign up an account.
- **Wallet Management**: Users are able to create a new wallet on the UI while also loading wallets that they have previously made on the application. Users can make multiple wallets.
- **Balance**: User is able to fetch the balance for their current wallet.
- **Message Signing**: Users are able to sign messages with their wallets private key.
- **Transaction**: A user is able to send a transaction from wallet to another by entering a destination address and an amount to send.
- **User-Friendly Interface**: The application has a clean, intuitive interface that simplifies the complexities of cryptocurrency management. It is implemented using JavaFX.


## Installation

To get started with DynamicSpencer, follow these simple steps:

1. **Clone the Repository**
   ```bash
   git clone https://github.com/SpencerKatz/DynamicSpencer.git
2. You will likely have to download JavaFX if you have not already.
Here is a link to download it: https://openjfx.io

# Notes
* Simple JUnit Tests located in tests folder under test-annotations.
* Sending Transaction
  * I was unable to test transaction sending as I do not have a wallet with a non-zero balance.
  I attempted to load test Ethereum onto a wallet, but websites seemed to indicate that I need to have
  a wallet with a non-zero amount of ethereum on it in order to request test Ethereum.

