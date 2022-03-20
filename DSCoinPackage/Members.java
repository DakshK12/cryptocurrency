package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.TreeNode;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public String next_string(String s){
   //increment by 1
   int i = Integer.parseInt(s);
   i++;
   return Integer.toString(i);
  }


  public List<Pair<String,TransactionBlock>> sort_list(List<Pair<String,TransactionBlock>> al){
      int n = al.size();
      Pair<String,TransactionBlock> temp = new Pair<>(null,null);
      for (int i=0;i<n;i++){
          for(int j=1;j<n-i;j++){
              if(al.get(j-1).first.compareTo(al.get(j).first)>0){
                  temp = al.get(j-1);
                  al.set(j-1,al.get(j));
                  al.set(j,temp);
              }
          }
      }

      return al;
  }

  public boolean compare_trans(Transaction t1 , Transaction t2){

      if(t1!=null && t2!=null) {
          if (!t1.coinID.equals(t2.coinID)) return false;
          if (!t1.Source.equals(t2.Source)) return false;
          if (!t1.Destination.equals(t2.Destination)) return false;
          if(!t1.coinsrc_block.equals(t2.coinsrc_block)) return false;
          return true;
      }
      else return false;
  }

  public boolean check_valid_transaction_malicious(DSCoin_Malicious DSObj , Transaction tt){
      TransactionBlock tb = DSObj.bChain.FindLongestValidChain();
      return tb.checkTransaction(tt);
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Pair<String,TransactionBlock> a = mycoins.get(0);
    mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = a.first;
    tobj.coinsrc_block = a.second;
    tobj.Source = this;
    int i=0;
    while(true){
     if(DSobj.memberlist[i].UID.equals(destUID)){
      tobj.Destination = DSobj.memberlist[i];
      break;
     }
     i++;
    }
    i = 0;
    while(in_process_trans[i]!=null) i++;
    in_process_trans[i] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }
/*
     public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
         Pair<String,TransactionBlock> a = mycoins.get(0);
         mycoins.remove(0);
         Transaction tobj = new Transaction();
         tobj.coinID = a.first;
         tobj.coinsrc_block = a.second;
         tobj.Source = this;
         int i=0;
         while(true){
             if(DSobj.memberlist[i].UID.equals(destUID)){
                 tobj.Destination = DSobj.memberlist[i];
                 break;
             }
             i++;
         }
         i = 0;
         while(in_process_trans[i]!=null) i++;
         in_process_trans[i] = tobj;
         DSobj.pendingTransactions.AddTransactions(tobj);
     }
     */


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {

    TransactionBlock it = DSObj.bChain.lastBlock;
    int flag = 0;
    int idx =0;
    int k = 0;
    while(it!=null){
      int n = it.trarray.length;
      for(int i=0;i<n;i++){
       if(compare_trans(it.trarray[i],tobj )){
        flag = 1;
        idx = i;
        break;
       }
      }
      if(flag==1) break;
      it = it.previous;
      k++; //k is number of blocks after required transaction block
    }
    if(it==null) throw new MissingTransactionException(); //means tobj not present in blockchain
    else{
     //it is required Transaction block
     List<Pair<String,String>> l1 = it.Tree.build_array(it.trarray,idx);

     //l1 is sibling coupled path to root for tobj

     List<Pair<String,String>> l2 = new ArrayList<Pair<String,String>>(k+2);
     List<Pair<String,String>> l3 = new ArrayList<>();


     if(it.previous==null){
      l2.add(0,new Pair<>("DSCoin",null));
     }
     else l2.add(0,new Pair<>(it.previous.dgst,null));



     // l2 has pairs in indices 0 to (k+1).
     TransactionBlock tb_iterator = DSObj.bChain.lastBlock;
    // int j = k+1;
     while(tb_iterator!=it){
      l3.add(new Pair<>(tb_iterator.dgst,tb_iterator.previous.dgst+"#"+tb_iterator.trsummary+"#"+tb_iterator.nonce));
     // j--;
      tb_iterator = tb_iterator.previous;
     }



     if(it.previous==null){
      l2.add(1,new Pair<>(it.dgst,"DSCoin" + "#" + it.trsummary + "#" + it.nonce));
     }
     else{
      l2.add(1,new Pair<>(it.dgst,it.previous.dgst+"#"+it.trsummary+"#" + it.nonce));
     }

        Collections.reverse(l3);
        for(int i=0;i<k;i++){
            l2.add(l3.get(i));
        }

     // both lists computed

     //delete form in_process_trans

     for(int p=0;p<100;p++){
      if(compare_trans(tobj,in_process_trans[p])){
       in_process_trans[p] = null;
       break;
      }
     }

     //add sent coin to dest my coin list

     tobj.Destination.mycoins.add(new Pair<>(tobj.coinID, it));

    tobj.Destination.mycoins = sort_list(tobj.Destination.mycoins);

     return new Pair<>(l1,l2);
    }
   // return null;
  }

  public void MineCoin(DSCoin_Honest DSObj) {

   int x = DSObj.bChain.tr_count-1;
   Transaction it = DSObj.pendingTransactions.firstTransaction; //it will be used to iterate over pending Transaction queue
   int count = 0; //number of valid transactions obtained till now
   Transaction[] tr_arr = new Transaction[x+1];

   while(count<x){
    if(DSObj.bChain.lastBlock.checkTransaction(DSObj.pendingTransactions.firstTransaction)){
     //if this transaction is valid
     //check if it is already present in tr_arr
     int flag = 0;
     for(int i=0;i<count;i++){
      if(tr_arr[i].equals(it)){
       flag = 1;
       break;
      }
     }
     if(flag==0){
        tr_arr[count] = it;
        count++;
        it = it.next;
        try {
            DSObj.pendingTransactions.RemoveTransaction();
        }
        catch (EmptyQueueException e){
            System.out.println("empty queue");
        }
     }
     else{
      it = it.next;
         try {
             DSObj.pendingTransactions.RemoveTransaction();
         }
         catch (EmptyQueueException e){
             System.out.println("empty queue");
         }
     }
    }
    else{
        //when 'it' is invalid transaction
        if(it.next==null){
            try {
                DSObj.pendingTransactions.RemoveTransaction();
            }
            catch (EmptyQueueException e){
                System.out.println("empty queue");
            }
        }
        else{
            it = it.next;
            try {
                DSObj.pendingTransactions.RemoveTransaction();
            }
            catch (EmptyQueueException e){
                System.out.println("empty queue");
            }
        }
    }
   }
   Transaction miner_reward = new Transaction();
   miner_reward.coinID = next_string(DSObj.latestCoinID);
   miner_reward.Source = null;
   miner_reward.Destination = this;
   miner_reward.coinsrc_block = null;
   tr_arr[x] = miner_reward;

   TransactionBlock tb_miner = new TransactionBlock(tr_arr);
   DSObj.bChain.InsertBlock_Honest(tb_miner);
   this.mycoins.add(new Pair<>(miner_reward.coinID,tb_miner));

      this.mycoins = sort_list(this.mycoins);

   DSObj.latestCoinID = miner_reward.coinID;

  }  

  public void MineCoin(DSCoin_Malicious DSObj) {

   int x = DSObj.bChain.tr_count - 1;
   Transaction it = DSObj.pendingTransactions.firstTransaction;
   int count = 0;
   Transaction[] tr_arr = new Transaction[x+1];
    while(count<x && DSObj.pendingTransactions.firstTransaction!=null ){
        if(check_valid_transaction_malicious(DSObj,it)){                              //check_valid_transaction_malicious(DSObj,it)
            //it is valid transaction
            //check if it is already present in tr_arr
            int flag = 0;
            for(int i=0;i<count;i++){
                if(tr_arr[i].equals(it)){
                    flag = 1;
                    break;
                }
            }
            if(flag==0){
                tr_arr[count] = it;
                count++;
                it = it.next;
                try {
                    DSObj.pendingTransactions.RemoveTransaction();
                }
                catch (EmptyQueueException e){
                    System.out.println("empty queue");
                }
            }
            else{
                it = it.next;
                try {
                    DSObj.pendingTransactions.RemoveTransaction();
                }
                catch (EmptyQueueException e){
                    System.out.println("empty queue");
                }
            }
        }
        else{
            //when 'it' is invalid transaction
            if(it.next==null){
                try {
                    DSObj.pendingTransactions.RemoveTransaction();
                }
                catch (EmptyQueueException e){
                    System.out.println("empty queue");
                }
            }
            else{
                it = it.next;
                try {
                    DSObj.pendingTransactions.RemoveTransaction();
                }
                catch (EmptyQueueException e){
                    System.out.println("empty queue");
                }
            }
        }
    }
        if(count==x) {
            Transaction miner_reward = new Transaction();
            miner_reward.coinID = next_string(DSObj.latestCoinID);
            miner_reward.Source = null;
            miner_reward.Destination = this;
            miner_reward.coinsrc_block = null;
            tr_arr[count] = miner_reward;

            TransactionBlock tb_miner = new TransactionBlock(tr_arr);
            DSObj.bChain.InsertBlock_Malicious(tb_miner);
            this.mycoins.add(new Pair<>(miner_reward.coinID, tb_miner));

        this.mycoins = sort_list(this.mycoins);
            DSObj.latestCoinID = miner_reward.coinID;
        }
  }
}
