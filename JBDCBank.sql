create table superUsers(
    superuser_id number(10) primary key,
    username varchar2(255) unique not null,
    password varchar2(255) not null
);

create table users(
    user_id number(10) primary key,
    username varchar2(255) unique not null,
    password varchar2(255) not null
    --constraint uniqueUsername check(username not in superUsers.username)
);

create table accounts(
    account_id number(10) primary key,
    name varchar2(255),
    balance binary_float default 0,
    user_id number(10) not null,
    
    constraint positive_balance check (balance >= 0),
    constraint fk_accounts_userid 
    foreign key (user_id) references users(user_id) on delete cascade
);

create table transactions(
    transaction_id number(10) primary key,
    amount binary_float default 0,
    transaction_date timestamp default SYSDATE,
    account_id number(10) not null,
    
    constraint fk_transactions_accounts
    foreign key(account_id) references accounts(account_id) on delete cascade
);

insert into superUsers(superuser_id, username, password) values (1, 'wegert', 'admin');

--TODO: fix counter going up when nothing is inserted..
create sequence superuser_id_counter start with 2 increment by 1;
create sequence user_id_counter start with 1 increment by 1;
create sequence account_id_counter start with 1 increment by 1;
create sequence transaction_id_counter start with 1 increment by 1;

select * into transactions from transactions order by transaction_id;

select * from transactions order by transaction_id;

create or replace procedure addSuperUser(nameinput varchar2, pw varchar2, superUserid_OUT out number) as
begin
    insert into superusers values (superuser_id_counter.nextval, nameinput, pw);
    if sql%rowcount = 0 then
        raise ACCESS_INTO_NULL;
    end if;
    superUserid_OUT := superuser_id_counter.CURRVAL;
end addSuperUser;
/
commit;
call addSuperUser('wegert','admin');
create or replace procedure viewSuperUser(username_INPUT varchar2, superuser_ID_OUTPUT out number, password_OUTPUT out varchar2) as
begin
    select superuser_ID into superuser_ID_OUTPUT from superusers where username = username_INPUT;
    select password into password_OUTPUT from superusers where username = username_INPUT;
end viewSuperUser;
/

create or replace procedure addUser(nameinput varchar2, pw varchar2, userid_OUT out number) as
begin
    insert into users values (user_id_counter.nextval, nameinput, pw);
    userid_OUT := user_id_counter.CURRVAL;
end addUser;
/

create or replace procedure addSuperUser(nameinput varchar2, pw varchar2, userid_OUT out number) as
begin
    insert into superusers values (superuser_id_counter.nextval, nameinput, pw);
    userid_OUT := user_id_counter.CURRVAL;
end addSuperUser;
/

create or replace procedure updateUser(oldUsername varchar2, oldPassword varchar2, 
                                       newUsername varchar2, newPassword varchar2) as
begin
    update users set username = newUsername, password = newPassword where username = oldUsername and password = oldPassword;
end updateUser;
/

create or replace procedure updateUsername(oldUsername varchar2, newUsername varchar2, foundit out number) as
begin
    update users set username = newUsername where username = oldUsername;
    if sql%rowcount > 0 then
        foundit := 1;
    else
        foundit := 0;
    end if;
end updateUsername;
/

create or replace procedure updatePassword(username_INPUT varchar2, newPassword varchar2, foundit out number) as
begin
    update users set password = newPassword where username = username_INPUT;
    if sql%rowcount > 0 then
        foundit := 1;
    else
        foundit := 0;
    end if;
end updatePassword;
/

create or replace procedure deleteUser(username_INPUT varchar2, foundit out number) as
begin
    delete from users where username = username_INPUT;
    if sql%rowcount > 0 then
        foundit := 1;
    else
        foundit := 0;
    end if;
end deleteUser;
/

create or replace procedure getUserInfo(username_INPUT varchar2, password_OUT out varchar2, user_id_OUT out number, numAccounts out number, totalbalance out binary_float) as
current_user_id number;
begin
    select password into password_OUT from users where username = username_INPUT;
    select user_id into current_user_id from users where username = username_INPUT;
    select sum(balance) into totalbalance from accounts where user_id = current_user_id;
    user_id_OUT := current_user_id;
    select count(user_id) into numAccounts from accounts where user_id = current_user_id;
end getUserInfo;
/

create or replace procedure getUserInfoByID(user_id_INPUT number, username_OUT out varchar2, password_OUT out varchar2, numAccounts out number, totalbalance out binary_float) as
begin
    select password into password_OUT from users where user_id = user_id_INPUT;
    select username into username_OUT from users where user_id = user_id_INPUT;
    select sum(balance) into totalbalance from accounts where user_id = user_id_INPUT;
    select count(user_id_INPUT) into numAccounts from accounts where user_id = user_ID_INPUT;
end getUserInfoByID;
/

create or replace procedure getUserID(username_INPUT varchar2, password_INPUT varchar2, user_id_OUT out number) as
begin
    select user_id into user_id_OUT from users where username = username_INPUT and password = password_INPUT;
end getUserID;
/

create or replace procedure getUserAccountInfo(user_id_INPUT number, numaccounts out number, totalbalance out binary_float) as
begin
    select count(user_id) into numaccounts from accounts where user_id = user_id_INPUT;
    select sum(balance) into totalbalance from accounts where user_id = user_id_INPUT;
end getUserAccountInfo;
/
commit;

-- USER-ACCESSIBLE PROCEDURES: 
create or replace procedure addAccountByUsername(name_INPUT varchar2, initial_deposit binary_float, username_INPUT varchar2) as
    user_id_INPUT users.user_id%type;
begin
    select user_id into user_id_INPUT from users where username = username_INPUT;
    insert into accounts values (account_id_counter.nextval, name_INPUT, initial_deposit, user_id_INPUT);
end addAccountByUsername;
/

create or replace procedure addAccountbyUserID(user_id_INPUT number, initial_deposit binary_float, name_INPUT varchar2) as
begin
    insert into accounts values (account_id_counter.nextval, name_INPUT, initial_deposit, user_id_INPUT);
end addAccountbyUserID;
/

create or replace procedure deleteAccount(name_INPUT varchar2, username_INPUT varchar2, password_INPUT varchar2) as
    user_id_INPUT users.user_id%type;
begin
    select user_id into user_id_INPUT from users where username = username_INPUT and password = password_INPUT;
    delete from accounts where name = name_INPUT and user_id = user_id_INPUT;
end deleteAccount;
/

create or replace procedure deleteAccountbyUserID(user_id_INPUT number, name_INPUT varchar2, success out int) as
begin
    delete from accounts where name = name_INPUT and user_id = user_id_INPUT;
    success := sql%rowcount;
end deleteAccountbyUserID;
/

create or replace procedure commitTransaction(amount_INPUT binary_float, account_id_INPUT number) as
    currentBalance accounts.balance%type;
begin
    select balance into currentBalance from accounts where account_id = account_id_INPUT;
    if currentBalance + amount_INPUT >= 0 then
        insert into transactions values (transaction_id_counter.nextval, amount_INPUT, SYSDATE, account_id_INPUT);
    end if;
end commitTransaction;
/

create or replace procedure withdrawOrDeposit(account_id_INPUT number, amount_INPUT binary_float) as
    currentBalance accounts.balance%type;
begin
    select balance into currentBalance from accounts where account_id = account_id_INPUT;
    if currentBalance + amount_INPUT >= 0 then
        commitTransaction(amount_INPUT, account_id_INPUT);
        update accounts set balance = (currentBalance + amount_INPUT) where account_id = account_id_INPUT;
    else
        raise ACCESS_INTO_NULL;
    end if;
end withdrawOrDeposit;
/

create or replace procedure commitTransactionBool(amount_INPUT binary_float, account_id_INPUT varchar2, succeeded out number) as
    currentBalance accounts.balance%type;
begin
    select balance into currentBalance from accounts where account_id = account_id_INPUT;
    if (currentBalance + amount_INPUT >= 0) then
        insert into transactions values (transaction_id_counter.nextval, amount_INPUT, SYSDATE, account_id_INPUT);
        succeeded := 1;
    else
        succeeded := 0;
    end if;
end commitTransactionBool;
/

create or replace procedure transferFundsbyUserID( -- FROM 1 INTO 2
name1 varchar2, name2 varchar2, transfer_amount binary_float, 
user_id_INPUT number, succeeded out number) as
    balance1 accounts.balance%type;
    balance2 accounts.balance%type;
    account1_ID accounts.account_id%type;
    account2_ID accounts.account_id%type;
begin
    select account_id into account1_ID from accounts where name = name1 and user_id = user_id_INPUT;
    select account_id into account2_ID from accounts where name = name2 and user_id = user_id_INPUT;
    select balance into balance1 from accounts where account_id = account1_ID;
    select balance into balance2 from accounts where account_id = account2_ID;
    
    if balance1 - transfer_amount >= 0 then-- good to go
        update accounts set balance = (balance1 - transfer_amount) where account_id = account1_ID;
        update accounts set balance = (balance2 + transfer_amount) where account_id = account2_ID;
        commitTransaction(-transfer_amount, account1_ID);
        commitTransaction(transfer_amount,  account2_ID);
        succeeded := 1;
        commit;
    else
        succeeded := 0;
    end if;
end transferFundsbyUserID;
/

create or replace procedure transferFundsBetweenAccounts( -- FROM 1 INTO 2
account1_id accounts.account_id%type, account2_id accounts.account_id%type, transfer_amount accounts.balance%type,
succeeded out number) as
    balance1 accounts.balance%type;
    balance2 accounts.balance%type;

begin
    select balance into balance1 from accounts where account_id = account1_ID;
    select balance into balance2 from accounts where account_id = account2_ID;
    
    if balance1 - transfer_amount >= 0 then-- good to go
        update accounts set balance = (balance1 - transfer_amount) where account_id = account1_ID;
        update accounts set balance = (balance2 + transfer_amount) where account_id = account2_ID;
        commitTransaction(-transfer_amount, account1_ID);
        commitTransaction(transfer_amount,  account2_ID);
        succeeded := 1;
        commit;
    else
        succeeded := 0;
    end if;
end transferFundsBetweenAccounts;
/
commit;
--create or replace procedure transferFundsBetweenUsers()
--create or replace procedure transferFundsBetweenUsersWithAccountNames()

create or replace procedure getBalance(name_INPUT varchar2, user_id_INPUT number, currentBalance out binary_float) as
balanceCount number(10);
begin
    select count(balance) into balanceCount from accounts where name = name_INPUT and user_id = user_id_INPUT;

    if balanceCount = 0 then
        raise ACCESS_INTO_NULL;
    end if;
    
    select balance into currentBalance from accounts where name = name_INPUT and user_id = user_id_INPUT;

end getBalance;
/

create or replace procedure getLargestBalance(user_id_INPUT number, accountName out varchar2, currentBalance out binary_float) as
highestbalance accounts.balance%type;
maxaccountid accounts.account_id%type;
begin
    select max(balance) into highestbalance from accounts where user_id = user_id_INPUT;
    select account_id into maxaccountid from accounts where balance = highestbalance and user_id = user_id_INPUT;
    select balance into currentBalance from accounts where account_id = maxaccountid;
    select name into accountName from accounts where account_id = maxaccountid;
end getLargestBalance;
/

create or replace procedure getAccount(account_ID_OUTPUT out number, name_INPUT varchar2, balance_OUTPUT out binary_float, user_id_INPUT number) as
begin
    select account_id into account_ID_OUTPUT from accounts where name = name_INPUT and user_id = user_id_INPUT;
    select balance into balance_OUTPUT from accounts where name = name_INPUT and user_id = user_id_INPUT;
end getAccount;
/


create or replace procedure getMaxAccount(account_ID_OUTPUT out number, name_OUTPUT out varchar2, balance_OUTPUT out binary_float, user_id_INPUT number) as
begin
    select max(balance) into balance_OUTPUT from accounts where user_id = user_ID_INPUT;
    select account_id into account_ID_OUTPUT from accounts where user_id = user_ID_INPUT and balance = balance_OUTPUT;
    select name into name_OUTPUT from accounts where user_id = user_ID_INPUT and balance = balance_OUTPUT;
end getMaxAccount;
/


create or replace procedure getMaxAccountCursor(user_id_INPUT users.user_id%type, account_OUT out SYS_REFCURSOR) as
maxbalance accounts.balance%type;
begin
    select max(balance) into maxbalance from accounts where user_id = user_ID_INPUT;
    open account_OUT for
    select account_id, name, balance from accounts where user_id = user_id_INPUT and balance = maxbalance;
end getMaxAccountCursor;

create or replace procedure getBalances(user_id_INPUT number, name_balance_out out SYS_REFCURSOR) as
begin
    open name_balance_out for
    select name, balance from accounts where user_id = user_id_INPUT;
end getBalances;
/

create or replace procedure getTransactions(transactions_OUT out SYS_REFCURSOR) as
begin
    open transactions_OUT for
    select transaction_id, amount, transaction_date, account_id from Transactions;
end getTransactions;
/

create or replace procedure getTransactionsByUser(user_id_INPUT users.user_id%type, transactions_OUT out SYS_REFCURSOR) as
begin
    open transactions_OUT for
    select transactions.transaction_id, transactions.amount, transactions.transaction_date, 
    transactions.account_id from transactions join accounts on accounts.account_id = transactions.account_id
    where user_id = user_id_INPUT;
end getTransactionsByUser;
/

create or replace procedure getTransactionsByAccount(account_id_INPUT accounts.account_id%type, transactions_OUT out SYS_REFCURSOR) as
begin
    open transactions_OUT for
    select transaction_id, amount, transaction_date from Transactions where account_id = account_id_INPUT;
end getTransactionsByAccount;
/

create or replace procedure getTransactionByID(transaction_ID_INPUT transactions.transaction_id%type, transactions_OUT out SYS_REFCURSOR) as
begin
    open transactions_OUT for
    select transaction_id, amount, transaction_date, account_id from Transactions where transaction_id = transaction_ID_INPUT;
end getTransactionByID;
/
commit;

--if sql%rowcount > 0 then-- verify this works
--    succeeded := true;
--else
--    succeeded := false;
--end if;


exec adduser('bob', 'password');
exec addUser('joey','securepassword123');


select username, password from users;

commit;

call adduser('lisa','123456');
commit;

