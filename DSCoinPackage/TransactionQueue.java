package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
        if(firstTransaction==null){
          //means queue is empty
          firstTransaction = transaction;
          lastTransaction = transaction;
          numTransactions = 1;
          firstTransaction.next = firstTransaction.previous = lastTransaction.next = lastTransaction.previous = null;
          transaction.next = transaction.previous =null;
        }
        else{
          //queue not empty
          lastTransaction.next = transaction;
          transaction.next = null;
          transaction.previous = lastTransaction;
          lastTransaction = transaction;
          numTransactions += 1;
        }
  }
  
  public Transaction RemoveTransaction() throws EmptyQueueException {
      if(firstTransaction==null) throw new EmptyQueueException();
      else{
          Transaction a = firstTransaction;
          if(numTransactions==1){
              firstTransaction = lastTransaction = null;
          }
          else{
             firstTransaction = firstTransaction.next;
             firstTransaction.previous = null ;
          }
          numTransactions -= 1;
          return a;
      }
   // return null;
  }

  public int size() {
    return numTransactions;
  }
}
