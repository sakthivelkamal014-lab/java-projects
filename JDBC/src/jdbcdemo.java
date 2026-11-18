import java.sql.*;
import java.util.Scanner;


class jdbcdemo {
    static int option ;    
    public static void main(String args[]) throws Exception 
    {
        Connection cons = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {

            Scanner scan = new Scanner(System.in);
            cons = DriverManager.getConnection("jdbc:mysql://localhost:3306/std","root","Sakthivel2007");
            cons.setAutoCommit(false);    
            System.out.println("1.CREATE - NEW - ACCOUNT\n2.MONEY - TRANSFER\n3.REFER TRANSACTION\n4.EXIT");
            do
            {
                System.out.print("Enter your option: ");
                option = scan.nextInt();
                switch(option)
                {
                    case 1:
                        scan.nextLine();
                        System.out.print("Enter you name: ");
                        String pername = scan.nextLine();
                        scan.nextLine();
                        System.out.print("Enter you branch name: ");
                        String branchname = scan.nextLine();
                        System.out.print("Enter you account number: ");
                        int acc =scan.nextInt();
                        int newbala = 0;
                        String new_account = "INSERT INTO BANK VALUES (?,?,?,?)";
                        ps = cons.prepareStatement(new_account);
                        ps.setInt(1,acc);
                        ps.setString(2,pername);
                        ps.setString(3,branchname);
                        ps.setInt(4,newbala);
                        int result = ps.executeUpdate();
                        cons.commit();
                        System.out.println("Waiting...");
                        Thread.sleep(1000);
                        System.out.println("account created / ROWS AFFECTED: "+result);
                        break;
                    
                    case 2:
                        System.out.print("Enter you account number:");
                        int acc1 = scan.nextInt();            
                        System.out.print("Enter you ammount to send:");
                        int ammount = scan.nextInt();
                        System.out.print("Enter account number where need to transfer ");
                        int dacc = scan.nextInt();
                        String checkammount = "SELECT BANKBALANCE FROM BANK WHERE ACCNO = ?";
                        ps = cons.prepareStatement(checkammount);
                        ps.setInt(1,acc1);
                        ResultSet rsBalance = ps.executeQuery();
                        System.out.println("PROCESSING...");
                        Thread.sleep(2000);
                        
                        if(rsBalance.next())
                        {
                            int currentAmmount = rsBalance.getInt("BANKBALANCE");                                                    
                            if(currentAmmount < ammount) 
                            {
                                System.out.println("Transaction failed: Insufficient Ammount");
                                cons.rollback();
                            }
                            else
                            {
                                String reduce = "UPDATE BANK SET BANKBALANCE = BANKBALANCE - ? WHERE ACCNO = ?";
                                String add ="UPDATE BANK SET BANKBALANCE = BANKBALANCE + ? WHERE ACCNO = ?";                                
                                ps = cons.prepareStatement(reduce);
                                ps.setInt(1,ammount);
                                ps.setInt(2,acc1);
                                int reduceResult = ps.executeUpdate();                                                            
                                ps = cons.prepareStatement(add);
                                ps.setInt(1,ammount);
                                ps.setInt(2,dacc);
                                int addResult = ps.executeUpdate();
                                
                                System.out.println("Waiting...");
                                Thread.sleep(1000);
                                                                
                                if(reduceResult == 1 && addResult == 1)
                                {
                                    System.out.println("ROWS AFFECTED: 2");
                                    System.out.println("MONEY SEND");
                                    cons.commit(); 
                                }
                                else
                                {
                                    System.out.println("Transaction failed: Invalid receiver account");
                                    cons.rollback();
                                }
                            }
                        }
                        else 
                        {
                            System.out.println("Transaction failed: Sender account not found");
                            cons.rollback();
                        }
                        break;

                    case 3:
                        System.out.println("--TRANSACTION--");
                        System.out.println("Statement Loading");
                        Thread.sleep(2000);
                        ps = cons.prepareStatement("SELECT * FROM BANK"); 
                        rs = ps.executeQuery();                       
                        ResultSetMetaData  meta = ps.getMetaData();
                        int column = meta.getColumnCount();
                        for(int i = 1; i <= column; i++)
                        {
                            System.out.printf("%-15s", meta.getColumnName(i)+"\t");
                        }
                        System.out.print("\n");
                        while(rs.next())
                        {
                            for(int i = 1; i <= column; i++)
                            {
                                System.out.printf("%-15s",rs.getString(i)+"\t");
                            }
                            System.out.println("\n");
                        }
                        break;
                        
                    case 4:
                        System.out.println("Thank you welcome again!");
                        break;

                }
                
            }while(option != 4);
        }
                
        catch(Exception e)
        {
            if(cons != null)
            {
            cons.rollback();
            System.out.print(e);
            }            
        }
        
    }    
}
