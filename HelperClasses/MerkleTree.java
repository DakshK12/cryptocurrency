package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;
  TreeNode[] arr;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    numdocs = num_trans;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }

  public List<Pair<String,String>> build_array(Transaction[] tr,int idx){
      int n = tr.length;
      int N = 2*n-1;
      arr = new TreeNode[N];

      for(int i=0;i<N;i++){
      TreeNode nod = new TreeNode();
      arr[i] = nod;
      }

      Build(tr);
      arr[0] = rootnode;
      for(int i=0;i<N-n;i++){
        arr[2*i+1] = arr[i].left;
        arr[2*i+2] = arr[i].right;
      }
      //arr is complete

    List<Pair<String, String>> sibling = new ArrayList<Pair<String,String>>();
    int arr_idx = N - n + idx;
    while(arr_idx!=0){
      if(arr_idx % 2 ==0){
        sibling.add(new Pair<>(arr[arr_idx - 1].val,arr[arr_idx].val));
        arr_idx = (arr_idx-2)/2;
      }
      else{
        sibling.add(new Pair<>(arr[arr_idx].val,arr[arr_idx + 1].val));
        arr_idx = (arr_idx-1)/2;
      }
    }
    sibling.add(new Pair<>(arr[0].val,null));


    return sibling;
  }


}
