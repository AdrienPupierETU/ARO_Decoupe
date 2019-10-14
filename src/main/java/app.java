import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;

/*
tuto : https://gist.github.com/huberflores/2d422581dc6657badc21
 */

public class app {
    public static void main(String[] args){
        DecoupeSolver decoupe= initDecoupe();
        glp_prob lp =decoupe.createProb();
        SacSolver sacSolver;
        glp_prob lpSac;

        int motifSize=4;
        try{
            solve(lp);
            System.out.println(GLPK.glp_get_row_dual(lp,4));
            System.out.println(GLPK.glp_get_obj_val(lp));
            double sacOpt=2; // Z
            while(true){
                List<Double> rowDual= new ArrayList<>();
                List<Integer> newMotif= new ArrayList<>();
                for(int i=1;i<=motifSize;i++ ){
                       rowDual.add(GLPK.glp_get_row_dual(lp,i));
                }
                sacSolver = initSac(lp);
                lpSac = sacSolver.createProb();
                //mettre les bonne valeur dans obj
                solve(lpSac);


                sacOpt=GLPK.glp_get_obj_val(lpSac);

                if(sacOpt<1){
                    break;
                }
                for(int i=1;i<=motifSize;i++){
                    newMotif.add((int)Math.round(GLPK.glp_get_col_prim(lp,i)));
                }
                decoupe.addMotif(newMotif);
                solve(lp);
            }

            write_lp_solution(lp);
            //runSac(lp);
            // Free memory
            GLPK.glp_delete_prob(lp);
            GLPK.glp_delete_prob(lpSac);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void solve(glp_prob lp){
        glp_smcp parm;
        int ret;
        parm = new glp_smcp();
        GLPK.glp_init_smcp(parm);
        ret = GLPK.glp_simplex(lp, parm);
        if (ret != 0) {
            System.out.println("The problem could not be solved");
        }
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

    public static SacSolver initSac(glp_prob lp){
        List<Double> coefZSac = new ArrayList<>();

        int n = GLPK.glp_get_num_cols(lp);

        //System.out.println("La valeur de n est : "+n);

        for (int i = 1; i <= n; i++) {
            double val = GLPK.glp_get_row_dual(lp, i);
            coefZSac.add(val);
        }

        /*System.out.println("Test des valeurs dans coefZsac");
        for (int i = 0; i < n; i++) {
            System.out.println(coefZSac.get(i));
        }*/

        List<Integer> valueToReachSac= new ArrayList<>();
        valueToReachSac.add(100);
        SacSolver sacSolver = new SacSolver(coefZSac, valueToReachSac);

        List<Double> collone1= new ArrayList<>();
        for(int i=1; i<=n; i++){
            double val = GLPK.glp_get_col_prim(lp, i);
            collone1.add(val);
        }
        sacSolver.addcontrainteSac(collone1);

        /*System.out.println("Test des valeurs dans collone1");
        for (int i = 0; i<n; i++) {
            System.out.println(collone1.get(i));
        }*/

        return sacSolver;
    }
}

