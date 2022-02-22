# provision-vm
Ripple Coding Assignment

Due to time constraints and to define the scope of the assignment, I have made the app with the following assumptions and limitations: 
1. Email Id or Mobile Number will act as the username for each user since they are unique. Hence each user while signing up must provide either the email Id or mobile number. 
2. Role values for users are simple strings but ideally they should be a lookup value from a table. 
2. The provisioning of VMs is based on a set of possible configurations. Right now these are hardcoded but ideally they could be lookup tables in the DB. 
3. Certain requests would lead to different outputs based on the role of the user accessing them. For example, getAllVMs gives a list of VMs for all users if accessed by MASTER role.
4. Deletion of a user account by a MASTER user is done through userId as parameter. So an "/account/getUsers" endpoint is available to MASTER to access user Ids.
5. Passwords, though encrpyted, are never displayed in the output of API call.

Possible Configurations for VM
* OS: Windows, Linux, Mac
* RAM : 2^0 - 2^5 GB
* HardDisk : 500GB, 1TB, 2TB, 5TB
