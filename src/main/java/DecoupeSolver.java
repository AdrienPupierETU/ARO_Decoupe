import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;

public class DecoupeSolver {
    private List<List<Integer>> matriceMotif= new ArrayList<>();
    private List<Integer> coefZ;
    private List<Integer> valueToReach;

    public DecoupeSolver( List<Integer> coefZ, List<Integer> valueToReach) {
        this.coefZ = coefZ;
        this.valueToReach = valueToReach;
    }

    public void addMotif(List<Integer> motif){
        matriceMotif.add(motif);
    }
    public void addCoefz(int coef){
        coefZ.add(coef);
    }

    public glp_prob createProb(){
        glp_prob lp=GLPK.glp_create_prob();
        setContrainte(lp);
        GLPK.glp_set_obj_name(lp, "z");
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
        for(int i = 1; i<= coefZ.size(); i++){ //
            int value= coefZ.get(i-1);
            GLPK.glp_set_obj_coef(lp, i, value);
        }
        return lp;
    }
    public void printMotif(){
        for(int i=0;i<matriceMotif.get(0).size();i++){
            for(int j=0;j<matriceMotif.size();j++){
                System.out.print(" "+matriceMotif.get(j).get(i)+" ");
            }
            System.out.println("");
        }

    }

    private void setContrainte(glp_prob lp){
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;
        GLPK.glp_add_cols(lp, matriceMotif.size());
        for(int i=1;i<=matriceMotif.size();i++){
            GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_LO, 0, 0);
            GLPK.glp_set_col_name(lp, i, "x"+i);
        }
        ind = GLPK.new_intArray(matriceMotif.size()+1); // valeur = |collone| ?
        val = GLPK.new_doubleArray(matriceMotif.size()+1);
        GLPK.glp_add_rows(lp, matriceMotif.get(0).size());

        for(int i=1;i<=matriceMotif.get(0).size();i++){ // pour chaque ligne et donc chaque contrainte
            GLPK.glp_set_row_name(lp, i, "c"+i);
            int valuereach=valueToReach.get(i-1);
            GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_LO,valuereach , 0);
            for(int j=1;j<=matriceMotif.size();j++){ // pour chaque collone de cette ligne
                for(int k=1;k<=matriceMotif.size();k++) {
                    GLPK.intArray_setitem(ind, k, k); // set dans index
                }
                int value=matriceMotif.get(j-1).get(i-1);
                GLPK.doubleArray_setitem(val, j,value); //collone de la ligne courante
            }
            GLPK.glp_set_mat_row(lp, i, matriceMotif.size(), ind, val);
        }
        GLPK.delete_intArray(ind);
        System.out.println("bug here");
        GLPK.delete_doubleArray(val);
        System.out.println("salut");
    }


}
