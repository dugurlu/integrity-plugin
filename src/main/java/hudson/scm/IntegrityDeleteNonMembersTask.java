package hudson.scm;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import jenkins.SlaveToMasterFileCallable;

public class IntegrityDeleteNonMembersTask extends SlaveToMasterFileCallable<Boolean>
{
    private static final long serialVersionUID = 6452098989064436149L;
    private static final Logger LOGGER = Logger.getLogger("IntegritySCM");
    private final TaskListener listener;
    private final Run<?, ?> run;
    private FilePath alternateWorkspaceDir;
    private final IntegrityCMProject siProject;
    
    public IntegrityDeleteNonMembersTask( Run<?, ?> run,TaskListener listener,FilePath alternateWorkspaceDir,IntegrityCMProject siProject)
    {
        this.run = run;

        this.listener = listener;
        this.alternateWorkspaceDir = alternateWorkspaceDir;
        this.siProject = siProject;
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException{
        try 
        {
            deleteNonMembers(run, listener);
        }
        catch (SQLException e)
        {
            listener.getLogger().println("A SQL Exception was caught!"); 
            listener.getLogger().println(e.getMessage());
            LOGGER.log(Level.SEVERE, "SQLException", e);
            return false;       
        }
        return true;
    }

    /**
     * Delete all members in the build workspace that are not under version control
     * @param build
     * @param listener
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteNonMembers(Run<?, ?> run,TaskListener listener) throws SQLException, IOException, InterruptedException
    {
        List<Hashtable<CM_PROJECT, Object>> projectMembersList = DerbyUtils.viewProject(siProject.getProjectCacheTable());
    
        // Get all Integrity project members of the current build
        List<FilePath> projectMembers = new ArrayList<FilePath>();
        for (Hashtable<CM_PROJECT, Object> memberInfo : projectMembersList)
        {
            File targetFile = new File(alternateWorkspaceDir + memberInfo.get(CM_PROJECT.RELATIVE_FILE).toString());
            LOGGER.fine("Project Member: " + targetFile.getAbsolutePath());
            projectMembers.add(new FilePath(targetFile));
        }
        
        // Get all Integrity projects of the current build
        List<String> folderList = DerbyUtils.getDirList(siProject.getProjectCacheTable());
        for( String folder:folderList )
        {
            File targetFile = new File(alternateWorkspaceDir + folder);
            LOGGER.fine("Project Folder: " + targetFile.getAbsolutePath());
            projectMembers.add(new FilePath(targetFile));
        }
        
        // Delete all members and folders that are not part of the Integrity project
        deleteNonMembers(alternateWorkspaceDir, projectMembers, listener);

    }
    
    /**
     * Delete all members in {@code workspaceFolder} that are not {@code projectMembers}
     * @param workspaceFolder
     * @param projectMembers
     * @param listener
     * @throws IOException
     * @throws InterruptedException
     */
    private void deleteNonMembers(FilePath workspaceFolder, List<FilePath> projectMembers, TaskListener listener ) throws IOException, InterruptedException
    {
        List<FilePath> workspaceMembers = workspaceFolder.list();
        for( FilePath workspaceMember:workspaceMembers )
        {
            LOGGER.fine("Workspace Member: " + workspaceMember);
            if( workspaceMember.exists() && !projectMembers.contains(workspaceMember) )
            {     
                if( workspaceMember.isDirectory() )
                {
                    // It's possible that this is a folder in a project so we can't delete this folder (it can still have registered members somewhere beneath)  
                	// TODO: We need a way to determine folders that aren't part of the project so that we can delete unused folders
                    //listener.getLogger().println("Deleting folder " + workspaceMember + " because the folder does not exits in the Integrity project");
                    //workspaceMember.deleteRecursive();
                }
                else
                {
                    listener.getLogger().println("Deleting file " + workspaceMember + " because the member does not exist in the Integrity project" );
                    workspaceMember.delete();
                }
            }
            else
            {
                if(workspaceMember.isDirectory())
                {
                    deleteNonMembers(workspaceMember,projectMembers,listener);
                }
            }
        }
    }
}
