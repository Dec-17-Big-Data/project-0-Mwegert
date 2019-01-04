exec deleteUser('bob', 'password');
exec deleteUser('joey','securepassword123');

drop procedure getUserAccountInfo;
drop procedure getUserInfoByID;
drop procedure getUserInfo;
drop procedure updateUser;
drop procedure updatePassword;
drop procedure updateUsername;
drop procedure addAccount;
drop procedure addAccountbyUserID;
drop procedure deleteAccount;
drop procedure deleteAccountbyUserID;
drop procedure commitTransaction;
drop procedure transferFundsbyUserID;
drop procedure getBalance;



-- not accessible by regular user
drop procedure addSuperUser;
drop procedure addUser;
drop procedure deleteUser;


drop procedure commitTransaction;
drop procedure addAccount;
drop procedure addSuperUser;
drop procedure addUser;

drop sequence account_id_counter;
drop sequence transaction_id_counter;
drop sequence user_id_counter;
drop sequence superUser_id_counter;

delete from superUsers where (superuser_id = 1);

drop table transactions;
drop table accounts;
drop table users;
drop table superUsers;



