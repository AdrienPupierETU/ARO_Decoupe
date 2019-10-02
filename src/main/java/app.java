import org.gnu.glpk.*;

/*
tuto : https://gist.github.com/huberflores/2d422581dc6657badc21
 */

public class app {
    public static void main(String[] args){
        glp_prob lp;
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;
        glp_smcp parm;
        int ret;
        try{
            lp= GLPK.glp_create_prob();
            // creer collones
            GLPK.glp_add_cols(lp, 15);
            for(int i=1;i<=15;i++){
                GLPK.glp_set_col_name(lp, i, "x"+i);
                GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
                GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_LO, 0, 0);
            }
            //constrainte

            //memoire

            ind = GLPK.new_intArray(15); // valeur = |collone| ?
            val = GLPK.new_doubleArray(15);

            //create rows
            GLPK.glp_add_rows(lp, 4); // 4 contrainte, 1 par nombre de rouleaux qu'il faut
            //set row details
            //1 contrainte
            GLPK.glp_set_row_name(lp, 1, "c1");
            GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_LO, 97, 0); // on veut au moins 97 rouleaux a1 (45cm)
            for(int i=1;i<=9;i++) {
                GLPK.intArray_setitem(ind, i, i); // set dans index
            }
            GLPK.doubleArray_setitem(val, 1, 2); // set dans val => value
            GLPK.doubleArray_setitem(val, 2, 1);
            GLPK.doubleArray_setitem(val, 3, 1);
            GLPK.doubleArray_setitem(val, 4, 1);
            GLPK.doubleArray_setitem(val, 5, 1);
            GLPK.doubleArray_setitem(val, 6, 1);
            GLPK.doubleArray_setitem(val, 7, 1);
            GLPK.doubleArray_setitem(val, 8, 1);
            GLPK.doubleArray_setitem(val, 9, 1);
            GLPK.glp_set_mat_row(lp, 1, 9, ind, val);
            //2 contrainte
            /*GLPK.glp_set_row_name(lp, 1, "c2");
            GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_LO, 610, 0);
            GLPK.intArray_setitem(ind, 1, 2);
            GLPK.intArray_setitem(ind, 2, 3);
            int j=3;
            for(int i=10;i<=15;i++) {
                GLPK.intArray_setitem(ind, j, i);
                j++;
            }
            GLPK.doubleArray_setitem(val, 2, 1);
            GLPK.doubleArray_setitem(val, 3, 1);
            GLPK.doubleArray_setitem(val, 10, 2);
            GLPK.doubleArray_setitem(val, 11, 2);
            GLPK.doubleArray_setitem(val, 12, 2);
            GLPK.doubleArray_setitem(val, 13, 1);
            GLPK.doubleArray_setitem(val, 14, 1);
            GLPK.doubleArray_setitem(val, 15, 1);
            GLPK.glp_set_mat_row(lp, 2, 8, ind, val);*/ // Pb car doit etre continu je pense
            //3 contrainte

            //4 contrainte



            // Free memory
            GLPK.delete_intArray(ind);
            GLPK.delete_doubleArray(val);

            // Define objective
            GLPK.glp_set_obj_name(lp, "z");
            GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
            GLPK.glp_set_obj_coef(lp, 0, 0);
            for(int i=1;i<=15;i++){
                GLPK.glp_set_obj_coef(lp, i, 1.);
            }
            //solve
            parm = new glp_smcp();
            GLPK.glp_init_smcp(parm);
            ret = GLPK.glp_simplex(lp, parm);

            // Retrieve solution
            if (ret == 0) {
                write_lp_solution(lp);
            } else {
                System.out.println("The problem could not be solved");
            }

            // Free memory
            GLPK.glp_delete_prob(lp);

        }catch (Exception e){
            e.printStackTrace();
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
    }
}

