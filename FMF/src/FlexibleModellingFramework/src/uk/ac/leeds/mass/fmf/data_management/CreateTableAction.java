
package uk.ac.leeds.mass.fmf.data_management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class CreateTableAction implements ActionListener{

    private RegisteredDataSource rds;

    public CreateTableAction (RegisteredDataSource rds){this.rds = rds;}


    public void actionPerformed(ActionEvent e) {
        ApplicationInformation.getCurrent().getMainPanel("Create New Table - ",new CreateNewTable(rds));
    }

}
