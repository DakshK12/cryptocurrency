package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    int n = t.length;
    trarray = new Transaction[n];
    for(int i=0;i<n;i++){
      trarray[i] = t[i] ;
    }
    previous = null;
    dgst = null;
    Tree = new MerkleTree();
    trsummary= Tree.Build(trarray);
  }

  public boolean checkTransaction (Transaction t) {
    int flag = 1;
    if(t.coinsrc_block==null) return true;
    else{
      TransactionBlock it = this;
      int count  = 0;
      while(it!=t.coinsrc_block){
        //check if same coin present in any intermediate Transactionblock it
        int n = it.trarray.length;
        for(int i=0;i<n;i++){
            if(t.coinID.equals(it.trarray[i].coinID)){
              flag = 0;
              break;
            }
        }
        if(flag==0) break;
        it = it.previous;
        count++;
      }
      if(flag==0) return false;
      // now check in coinsrc_block
      int n = t.coinsrc_block.trarray.length; // right now it = t.coinsrc_block
      for(int i=0;i<n;i++){
        if(it.trarray[i].coinID.equals(t.coinID) && it.trarray[i].Destination.UID.equals(t.Source.UID)){
          flag =2;
          break;
        }
      }
      if(flag==2) return true;
      else return false;
    }
 //   return false;
  }
}
