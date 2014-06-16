/**
 * These files comprise an example of how to communicate between tools.
 * They show how to store data in a central singleton.
 * They show how to use the singleton to set up a observer/observable pattern where 
 * tools can register for, and distribute alerts about, data changes. The alerted observers 
 * can then go back to the singleton for the data.
 * The example gives two menu items with associated windows. The windows contain row and column 
 * information for data dragged into them. Data drags into one window update the other.<P>
 * Details of the tool setup are not here to keep the information limited to communication. 
 * for detailled comments and examples on how to set up tools more generally, see 
 * the tooltemplate package.
 * @see tooltemplates.tooltemplate
 */
package tooltemplates.toolcommunication;