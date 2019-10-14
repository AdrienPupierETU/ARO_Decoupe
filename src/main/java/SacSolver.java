import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;


//
public class SacSolver {
    private List<List<Double>> contrainteSac = new ArrayList<>();
    private List<Double> coefZSac = new ArrayList<>();
    private List<Integer> valueToReachSac = new ArrayList<>();
    private glp_prob lp;

    public void addcontrainteSac(List<Double> contrainte){
        contrainteSac.add(contrainte);
    }

    public SacSolver(List<Double> coefZSac, List<Integer> valueToReachSac){
        this.coefZSac = coefZSac;
        this.valueToReachSac = valueToReachSac;
        lp = GLPK.glp_create_prob();
    }

    public glp_prob createProb(){
        setContrainteSac();

        GLPK.glp_set_obj_name(lp, "z");
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);

        for(int i=1;i<=coefZSac.size();i++){
            double value=coefZSac.get(i-1);
            GLPK.glp_set_obj_coef(lp, i, value);
        }
        return lp;
    }

    private void setContrainteSac(){
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;


        GLPK.glp_add_cols(lp, coefZSac.size());

        for(int i=1;i<=contrainteSac.size();i++){
            GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_UP, 0, 0);
            GLPK.glp_set_col_name(lp, i, "y"+i);
        }

        ind = GLPK.new_intArray(contrainteSac.get(0).size()+1); // valeur = |collone| ?
        val = GLPK.new_doubleArray(contrainteSac.size()+1);

        GLPK.glp_add_rows(lp, contrainteSac.get(0).size());

        for(int i = 0; i < contrainteSac.get(0).size();i++){
            double value = contrainteSac.get(0).get(i);
            GLPK.doubleArray_setitem(val, i+1, value);
            GLPK.intArray_setitem(ind, i+1,i+1);
        }

        GLPK.glp_set_mat_row(lp,contrainteSac.get(0).size(), contrainteSac.size(), ind, val);
        GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP,0 , valueToReachSac.get(0));

        GLPK.delete_intArray(ind);
        GLPK.delete_doubleArray(val);
    }
}
