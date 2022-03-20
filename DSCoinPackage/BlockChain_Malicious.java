package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;


  static boolean check_first_four_zero_v2(String s){
    if(s.charAt(0)=='0' && s.charAt(1)=='0' && s.charAt(2)=='0' && s.charAt(3)=='0') return true;
    else return false;
  }

  static String next_string_v2(String s){
    //increment by 1
    int i = Integer.parseInt(s);
    i++;
    return Integer.toString(i);
  }


  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF obj = new CRF(64);
    //first check if every transaction in tB.trarray is valid
    int flag = 1;
    int n = tB.trarray.length;
    for(int i=0;i<n;i++){
      if(tB.previous==null) return true;
      if(!tB.previous.checkTransaction(tB.trarray[i])){
        flag = 0;
        break;
      }
    }

    if(flag==0) {
      return false;
    }

    //complete

    //check tB.trsummary is correctly computed using tB.trarray

    MerkleTree mt = new MerkleTree();
    String trsummary_correct = mt.Build(tB.trarray);
    if(!tB.trsummary.equals(trsummary_correct)) {
      return false;
    }

    //complete

    //check for dgst correctness

    if(tB.previous==null){
      //only one Transaction block
      if(!check_first_four_zero_v2(tB.dgst)) return false;
      String s = "1000000001";
      String ans = obj.Fn(start_string + "#" + tB.trsummary + "#" + s);
      while(!check_first_four_zero_v2(ans)){
        s =  next_string_v2(s);
        ans = obj.Fn(start_string + "#" + tB.trsummary + "#" + s);
      }
      if(!tB.dgst.equals(ans)) return false;
      if(!tB.nonce.equals(s)) return false;
    }

    else{
      //more than one Transaction block
      if(!check_first_four_zero_v2(tB.dgst)) return false;
      String s = "1000000001";
      String ans = obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + s);
      while(!check_first_four_zero_v2(ans)){
        s =  next_string_v2(s);
        ans = obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + s);
      }
      if(!tB.dgst.equals(ans)) return false;
      if(!tB.nonce.equals(s)) return false;

    }
    //complete

    return true;
  }

  public TransactionBlock FindLongestValidChain () {

    int n = 0;
    TransactionBlock ans_last_tb = null;       //storing last Transaction block of longest valid chain obtained till now
    int ans_max_len = 0 ;                    //storing length of longest valid chain obtained till now

    for(;n<100;n++){
      if(lastBlocksList[n]==null) break;
    }


    // n stores the number of non null elements in lastblockslist

    for(int i=0;i<n;i++){
      TransactionBlock curr_last_tb=null;
      int curr_max_len=0;

      TransactionBlock iterator_tb = null ;
      int iterator_max_len = 0;

      iterator_tb = lastBlocksList[i];
      while(iterator_tb!=null){
        if(!checkTransactionBlock(iterator_tb)) {
          curr_last_tb = iterator_tb;
        }
        iterator_tb = iterator_tb.previous;
      }
      if(curr_last_tb==null){
        //means all blocks were valid
        curr_last_tb = iterator_tb = lastBlocksList[i] ;
        while(iterator_tb!=null){
          iterator_max_len ++;
          iterator_tb = iterator_tb.previous;
        }
      }

      else {
        //atleast one invalid block present and curr_last_tb is block 8 in Figure 2
        curr_last_tb = curr_last_tb.previous;
        iterator_tb = curr_last_tb;
        while (iterator_tb != null) {
          iterator_max_len++;
          iterator_tb = iterator_tb.previous;
        }
      }

      curr_max_len = iterator_max_len;

      if(curr_max_len>ans_max_len){
        //change is required in answer
        ans_max_len = curr_max_len;
        ans_last_tb = curr_last_tb;
      }
      //else no change required

    }

    return ans_last_tb;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {


    CRF obj = new CRF(64);
    TransactionBlock last_block = FindLongestValidChain();

    if (last_block == null) {
      //this is the first insertion by mod
      lastBlocksList[0] = newBlock;
      newBlock.previous = null;

      String s = "1000000001";
      String ans = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      while (!check_first_four_zero_v2(ans)) {
        s = next_string_v2(s);
        ans = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      }

      newBlock.nonce = s;
      newBlock.dgst = ans;
    }

    else {
      String s = "1000000001";
      String ans = obj.Fn(last_block.dgst + "#" + newBlock.trsummary + "#" + s);
      while (!check_first_four_zero_v2(ans)) {
        s = next_string_v2(s);
        ans = obj.Fn(last_block.dgst + "#" + newBlock.trsummary + "#" + s);
      }

      newBlock.nonce = s;
      newBlock.dgst = ans;
      newBlock.previous = last_block;

      //now we need to update lastblockslist

      //2 cases a) if last_block present in  lastblockslist then simply replace last_block with newblock
      // b) if not present , then add newblock to end of array

      int n1 = 0;
      while (lastBlocksList[n1] != null) n1++;
      // n1 represents number of non-null elements in array i.e. number of last blocks
    //  System.out.println("n1 = " + n1);
      int flag = 0;
      for (int i = 0; i < n1; i++) {
        if (lastBlocksList[i].equals(last_block)) {
          flag = 1;
          lastBlocksList[i] = newBlock;
          break;
        }
      }
      if (flag == 0) {
        lastBlocksList[n1] = newBlock;
      }

    }
  }
}
