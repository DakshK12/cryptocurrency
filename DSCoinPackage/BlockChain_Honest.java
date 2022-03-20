package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public boolean check_first_four_zero(String s){
    if(s.charAt(0)=='0' && s.charAt(1)=='0' && s.charAt(2)=='0' && s.charAt(3)=='0') return true;
    else return false;
  }

  public String next_string(String s){
    //increment by 1
    int i = Integer.parseInt(s);
    i++;
    return Integer.toString(i);
  }


  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    if(lastBlock==null){
      //means blockchain is empty right now
      lastBlock = newBlock;
      lastBlock.previous = null;
      String s = "1000000001";
      String ans = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      while(!check_first_four_zero(ans)){
          s =  next_string(s);
          ans = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      }
      lastBlock.dgst = ans;
      lastBlock.nonce = s;
    }
    else{
      //blockchain is non empty
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
      String s = "1000000001";
      String ans = obj.Fn(lastBlock.previous.dgst + "#" + newBlock.trsummary + "#" + s);
      while(!check_first_four_zero(ans)){
        s =  next_string(s);
        ans = obj.Fn(lastBlock.previous.dgst + "#" + newBlock.trsummary + "#" + s);
      }
      lastBlock.dgst = ans;
      lastBlock.nonce = s;
    }
  }
}
