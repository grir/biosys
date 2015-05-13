import java.util.*;

class Molecule {
   int x;
   double pLeft;
   double pRight;
   /////////////////////////////////////////////////////////////
   public Molecule(int x){
      this.x=x;
   }
   public void jump(double rnd){
      if (rnd < pLeft) x--;
      else if (rnd > 1-pRight) x++;
   }
}
/////////////////////////////////////////////////////////////////////////

class MaterialEdge{
   // Material edge is absract "vector" (tube) where we define
   // the flow of some material   
   
   public int nBoxes;// [0] - left boundary, [nBoxes+1] - right b.
   
   public int totalFlow;  // total material flow

   public  Vector<Molecule> mols;  // 
   
   /////////////////////////////////////////////////////////////
   public MaterialEdge(int nBoxes){ 
         this.nBoxes = nBoxes; 
         this.mols =  new Vector<Molecule>();
   }
   /////////////////////////////////////////////////////////////
   public Vector<Integer> getAtX(int x){
      Vector<Integer> res = new Vector<Integer> ();
      for(int i=0;i<mols.size();i++)
         if(mols.get(i).x == x) res.add(i);
      return res;   
   }
   /////////////////////////////////////////////////////////////
   public int getNumberAtX(int x){
      int xx=x;
      if(x <= -1) xx=0;
      if(x >= nBoxes) xx = nBoxes;
      
      int res = 0;      
      for(int i=0; i<mols.size(); i++)
         if(mols.get(i).x == xx) res++;
      return res;   
   }
   /////////////////////////////////////////////////////////////
   public void setLeftBound(int n){
      if (mols.size()==0)   {
          for(int i=0;i<(n+2);i++)
             mols.add(new Molecule(0));
             totalFlow += n;
          return;   
      }
      int k = n - getNumberAtX(0);
      
      totalFlow += k;
        
      for(int i = 0;i < k;i++)
          mols.add(new Molecule(0));
   }
   
   /////////////////////////////////////////////////////////////
   public void setRightBound(int n){
      if (mols.size()==0)   {
          for(int i=0;i<(n+2);i++)
             mols.add(new Molecule(nBoxes));
             totalFlow += n;
          return;   
      }
      int k = n - getNumberAtX(nBoxes);
      totalFlow += k;  
      for(int i = 0;i < k;i++)
          mols.add(new Molecule(nBoxes));
   }
   //////////////////////////////////////////////////////////////
   public void randomJump(Random generator){
      int idx = generator.nextInt(mols.size());
      Molecule mol = mols.get(idx);
      int x = mol.x;   
      double nm1 = getNumberAtX(x-1);
      double n0 = getNumberAtX(x);
      double np1 = getNumberAtX(x+1);
      //double pm1 = hyster(nm1-n0);
      double pm1;
     
      // Trasiotion probs: (1/3) ^ {difference between concentrations 
      // in boxes} 
      if(nm1>n0) pm1=0;
      else pm1 =  1 - Math.pow(1.0/3, (n0-nm1));

      double pp1;
      if(np1>n0) pp1=0;
      else pp1 =  1 - Math.pow(1.0/3, (n0-np1));
      
      double p0 = 1.0/3;//hyster(0);      
      //double pp1 = hyster(np1-n0);
      double sm = pm1+p0+pp1;
      
      mol.pLeft = pm1/sm; 
      mol.pRight = pp1/sm;
      //System.out.println(""+pm1/sm+" "+p0/sm+" "+pp1/sm);
      mol.jump(generator.nextDouble());
   }
   /////////////////////////////////////////////////////////////////
   public void randomReaction(Random generator, MaterialEdge other, 
                              double reactionProb){
      // Random reaction is point "random"
      // We use another edge info to execute this reaction  
      
      int idx = generator.nextInt(mols.size());
      Molecule mol = mols.get(idx);
      int x = mol.x;   
      Vector<Integer> molOther = other.getAtX(x);
      for(int i=0;i<molOther.size();i++)
         if(generator.nextDouble() < reactionProb){
             mols.remove(idx);
             other.mols.remove(molOther.get(i)); 
             break;         
         }
   }
   
   //////////////////////////////////////////////////////      
   public static double hyster(double a){
        return Math.atan(a)/Math.PI + 0.5;
   }
   
}

class RDGraph {
   public MaterialEdge[][] rdMatrix;
   public 














}








public class Modelis03 {
   public static   Random  gen;   //generator
   public static   int nBoxes = 12;
   public static   MaterialEdge A = new MaterialEdge(nBoxes);
   public static   MaterialEdge B = new MaterialEdge(nBoxes);
   
   public static   int aN=1000; // initial value at tne one of the ends
   public static   int bN=1000; // initial value at tne one of the ends

   public static double reactProb = 0.1;
  
   
//////////////////////////////////////////////////////   
  
   public static void step(){
     /// step:
     // 1) reaction between A and B
     A.randomReaction(gen,  B, reactProb);  
     B.randomReaction(gen,  A, reactProb);  
     // jumps:
     A.randomJump(gen);
     B.randomJump(gen);   
     // "repair"  bounds:          
     A.setLeftBound(aN);
     B.setRightBound(bN);
   
   }
   
   public static void printMol(){
     
     for(int i=0;i <= nBoxes;i++){
         Vector<Integer> vecA = A.getAtX(i);
         System.out.print("["+vecA.size()+"]");
     }    

     System.out.println(" "+B.totalFlow+"---> A");

     for(int i=0;i <= nBoxes;i++){
         Vector<Integer> vecB = B.getAtX(i);
         System.out.print("["+vecB.size()+"]");
     }    

     System.out.println(" "+B.totalFlow+"---> B");
   
   }
//////////////////////////////////////////////////////   
   
   public static void main(String[] args){
      gen = new Random();
   
      
      A.setLeftBound(aN);
      B.setRightBound(bN);
         
      
      for(int i=0;i<100000;i++){
         step();
         if(i%1000==0)  
           printMol();
      }
   
   }

}

