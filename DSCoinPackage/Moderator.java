package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
 {

  public String next_string(String s){
   //increment by 1
   int i = Integer.parseInt(s);
   i++;
   return Integer.toString(i);
  }

  public String prev_string(String s){
   //decrease by 1
   int i = Integer.parseInt(s);
   i--;
   return Integer.toString(i);
  }


  public String give_coin_id(int coin_number){
   String s = "100000";
   for(int i=0;i<coin_number;i++){
    s = next_string(s);
   }
   return s;
  }


  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {

   Members mm = new Members();
   mm.UID = "Moderator";

   int number_of_tb = coinCount / DSObj.bChain.tr_count;
   String coin_start = "100000";

   int number_of_member = DSObj.memberlist.length;

   for(int i=0;i<coinCount;i++){
      Transaction t = new Transaction();
      t.Source = mm;
      t.coinsrc_block = null;
      t.Destination = DSObj.memberlist[i % number_of_member];
      t.coinID = coin_start;
      //DSObj.memberlist[i % number_of_member].mycoins.add(new Pair<>(t.coinID,))
      coin_start = next_string(coin_start);

      //now add this transaction to tq
    DSObj.pendingTransactions.AddTransactions(t);
   }
   //pending transaction  is built
   DSObj.latestCoinID = prev_string(coin_start);

   int coin_number = 0;
   for(int i=1;i<=number_of_tb;i++){
    // moderator is going to create ith tb

    Transaction[] tr_array = new Transaction[DSObj.bChain.tr_count];
    for(int j=0;j<DSObj.bChain.tr_count;j++){
     //take the first tr_count transactions and create a block

     try {
      tr_array[j] = DSObj.pendingTransactions.RemoveTransaction();
     }
     catch (EmptyQueueException e){
      System.out.println("Empty queue");
     }


    }
    TransactionBlock tb = new TransactionBlock(tr_array);

    for(int k=0;k<DSObj.bChain.tr_count;k++){
     DSObj.memberlist[coin_number % number_of_member].mycoins.add(new Pair<>(give_coin_id(coin_number),tb));
     coin_number ++;
    }

    //insert this tb to block chain
    DSObj.bChain.InsertBlock_Honest(tb);
   }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
   Members mm = new Members();
   mm.UID = "Moderator";

   int number_of_tb = coinCount / DSObj.bChain.tr_count;
   String coin_start = "100000";

   int number_of_member = DSObj.memberlist.length;

   for(int i=0;i<coinCount;i++){
    Transaction t = new Transaction();
    t.Source = mm;
    t.coinsrc_block = null;
    t.Destination = DSObj.memberlist[i % number_of_member];
    t.coinID = coin_start;
    //DSObj.memberlist[i % number_of_member].mycoins.add(new Pair<>(t.coinID,))
    coin_start = next_string(coin_start);

    //now add this transaction to tq
    DSObj.pendingTransactions.AddTransactions(t);
   }
   //pending transaction  is built
   DSObj.latestCoinID = prev_string(coin_start);

   int coin_number = 0;
   for(int i=1;i<=number_of_tb;i++){
    // moderator is going to create ith tb

    Transaction[] tr_array = new Transaction[DSObj.bChain.tr_count];
    for(int j=0;j<DSObj.bChain.tr_count;j++){
     //take the first tr_count transactions and create a block

     try {
      tr_array[j] = DSObj.pendingTransactions.RemoveTransaction();
     }
     catch (EmptyQueueException e){
      System.out.println("Empty queue");
     }
    }
    TransactionBlock tb = new TransactionBlock(tr_array);

    for(int k=0;k<DSObj.bChain.tr_count;k++){
     DSObj.memberlist[coin_number % number_of_member].mycoins.add(new Pair<>(give_coin_id(coin_number),tb));
     coin_number ++;
    }
    //insert this tb to block chain
    DSObj.bChain.InsertBlock_Malicious(tb);
   }
  }
}
