DECLARE 
   a varchar2(255); 
   b varchar2(255); 
   c number(10);
begin
    a := 'bob';
    b := 'bobby';
    updateUsername(a, b, c);
    if c = 1 then
        dbms_output.put_line('TRUE');
    else
        dbms_output.put_line('FALSE');
    end if;
end;
/

DECLARE 
   a varchar2(255); 
   b varchar2(255); 
   c number(10);
   d number(10);
begin
    a := 'quintin';
    getUserInfo(a,b,c,d);
    dbms_output.put_line(a);
    dbms_output.put_line(b);
    dbms_output.put_line(c);
    dbms_output.put_line(d);
end;
/

begin
    deleteAccountbyUserID(8,'Primary Account');
end;
/

select count(account_id) as numaccounts, sum(balance) as totalbalance 
from accounts group by user_id;


