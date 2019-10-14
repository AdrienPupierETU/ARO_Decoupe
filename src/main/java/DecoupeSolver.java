import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;

public class DecoupeSolver {
    private List<List<Integer>> matriceMotif= new ArrayList<>();
    private List<Integer> CoefZ= new ArrayList<>();
    private List<Integer> valueToReach= new ArrayList<>();
    private glp_prob lp;

    public DecoupeSolver( List<Integer> coefZ, List<Integer> valueToReach) {
        CoefZ = coefZ;
        this.valueToReach = valueToReach;
        lp=GLPK.glp_create_prob();
    }

    public void addMotif(List<Integer> motif){
        matriceMotif.add(motif);
    }

    public glp_prob createProb(){
        setContrainte();
        GLPK.glp_set_obj_name(lp, "z");
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
        for(int i=1;i<=CoefZ.size();i++){ //
            int value=CoefZ.get(i-1);
            GLPK.glp_set_obj_coef(lp, i, value);
        }
        return lp;
    }

    private void setContrainte(){
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;
        GLPK.glp_add_cols(lp, matriceMotif.size());
        for(int i=1;i<=matriceMotif.size();i++){
            GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_LO, 0, 0);
            GLPK.glp_set_col_name(lp, i, "x"+i);
        }
        ind = GLPK.new_intArray(matriceMotif.size()); // valeur = |collone| ?
        val = GLPK.new_doubleArray(matriceMotif.size());
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
        GLPK.delete_doubleArray(val);
    }


}
