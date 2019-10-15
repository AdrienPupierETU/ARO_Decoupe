import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;

/*
tuto : https://gist.github.com/huberflores/2d422581dc6657badc21
 */

public class app {
    final static int TAILLE_MAX=100;
    public static void main(String[] args){
        DecoupeSolver decoupe= initDecoupe();
        SacSolver sacSolver;

        int motifSize=4;
        try{
            double sacOpt=2; // Z
            while(true){
                glp_prob lpDecoupe =decoupe.createProb();
                solveFrac(lpDecoupe);
                List<Double> rowDual= new ArrayList<>();
                List<Integer> newMotif= new ArrayList<>();

                for(int i=1;i<=motifSize;i++ ){
                       rowDual.add(GLPK.glp_get_row_dual(lpDecoupe,i));
                }
                sacSolver = initSac(rowDual);
                glp_prob lpSac = sacSolver.createProb();
                //mettre les bonne valeur dans obj

                solveInt(lpSac);
                sacOpt=GLPK.glp_mip_obj_val(lpSac);
                if(sacOpt<=1){
                    System.out.println("Solution finale : ");
                    write_lp_solution(lpDecoupe);
                    decoupe.printMotif();
                    break;
                }
                for(int i=1;i<=motifSize;i++){
                    double value = GLPK.glp_mip_col_val(lpSac,i);
                    newMotif.add((int)value);
                }
                decoupe.addMotif(newMotif);
                decoupe.addCoefz(1);
                GLPK.glp_delete_prob(lpDecoupe);
                GLPK.glp_delete_prob(lpSac);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void solveFrac(glp_prob lp){
        glp_smcp parm;
        int ret;
        parm = new glp_smcp();
        GLPK.glp_init_smcp(parm);
        ret = GLPK.glp_simplex(lp, parm);
        if (ret != 0) {
            System.out.println("The problem could not be solved");
        }
    }
    public static void solveInt(glp_prob lp){
        glp_iocp parm;
        int ret;
        parm = new glp_iocp();
        GLPK.glp_init_iocp(parm);
        parm.setPresolve(GLPKConstants.GLP_ON);
        ret = GLPK.glp_intopt(lp, parm);
        if (ret != 0) {
            System.out.println("The problem could not be solved");
        }
    }

    /** credit to : https://gist.github.com/huberflores/2d422581dc6657badc21
     * write simplex solution
     * @param lp problem
     */
    static void write_lp_solution(glp_prob lp) {
        int i;
        int n;
        String name;
        double val;

        name = GLPK.glp_get_obj_name(lp);
        val = GLPK.glp_get_obj_val(lp);
        System.out.print(name);
        System.out.print(" = ");
        System.out.println(val);
        n = GLPK.glp_get_num_cols(lp);
        for (i = 1; i <= n; i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_get_col_prim(lp, i);
            System.out.print(name);
            System.out.print(" = ");
            System.out.println(val);
        }
        System.out.println();
    }

    public static SacSolver initSac(List<Double> dualValue){

        List<Double> tailleCoupe= new ArrayList<>();
        tailleCoupe.add(45.);
        tailleCoupe.add(36.);
        tailleCoupe.add(31.);
        tailleCoupe.add(14.);

        SacSolver sacSolver = new SacSolver(dualValue,tailleCoupe, TAILLE_MAX);

        return sacSolver;
    }

    public static DecoupeSolver initDecoupe(){
        List<Integer> coefZ = new ArrayList<>();
        coefZ.add(1);
        coefZ.add(1);
        coefZ.add(1);
        coefZ.add(1);
        List<Integer> valueToReach= new ArrayList<>();
        valueToReach.add(97);
        valueToReach.add(610);
        valueToReach.add(395);
        valueToReach.add(211);
        DecoupeSolver decoupe= new DecoupeSolver(coefZ,valueToReach);
        List<Integer> collone1= new ArrayList<>();
        collone1.add(2);
        collone1.add(0);
        collone1.add(0);
        collone1.add(0);
        decoupe.addMotif(collone1);
        List<Integer> collone2= new ArrayList<>();
        collone2.add(0);
        collone2.add(2);
        collone2.add(0);
        collone2.add(0);
        decoupe.addMotif(collone2);
        List<Integer> collone3= new ArrayList<>();
        collone3.add(0);
        collone3.add(0);
        collone3.add(3);
        collone3.add(0);
        decoupe.addMotif(collone3);
        List<Integer> collone4= new ArrayList<>();
        collone4.add(0);
        collone4.add(0);
        collone4.add(0);
        collone4.add(7);
        decoupe.addMotif(collone4);
        return decoupe;
    }
}

