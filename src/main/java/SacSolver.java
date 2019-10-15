import org.gnu.glpk.*;

import java.util.ArrayList;
import java.util.List;


//
public class SacSolver {
    private List<Double> contrainteSac = new ArrayList<>();
    private List<Double> coefZSac;
    private int maxtaille;
    private glp_prob lp;

    public SacSolver(List<Double> coefZSac,List<Double>contrainteSac, int maxtaille){
        this.coefZSac = coefZSac;
        this.contrainteSac=contrainteSac;
        this.maxtaille = maxtaille;
        lp = GLPK.glp_create_prob();
    }


    public void setCoefZ(List<Double> coefZ){
        this.coefZSac=coefZ;
    }

    public glp_prob createProb(){
        setContrainteSac();

        GLPK.glp_set_obj_name(lp, "w");
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
            GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_IV);
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_LO, 0, 0);
            GLPK.glp_set_col_name(lp, i, "y"+i);
        }

        ind = GLPK.new_intArray(contrainteSac.size()+1); // valeur = |collone| ?
        val = GLPK.new_doubleArray(contrainteSac.size()+1);

        GLPK.glp_add_rows(lp,1);
        GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP,0 ,maxtaille );
        for(int i = 1; i <= contrainteSac.size();i++){
            for(int k=1;k<=contrainteSac.size();k++) {
                GLPK.intArray_setitem(ind, k, k); // set dans index
            }
            double value = contrainteSac.get(i-1);
            GLPK.doubleArray_setitem(val, i, value);
        }

        GLPK.glp_set_mat_row(lp,1, contrainteSac.size(), ind, val);


        GLPK.delete_intArray(ind);
        GLPK.delete_doubleArray(val);
    }
}
