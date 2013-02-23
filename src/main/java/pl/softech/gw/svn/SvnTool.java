package pl.softech.gw.svn;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 *
 * @author ssledz
 */
public class SvnTool {

    private class SVNEventHandlerImpl implements ISVNEventHandler {

        @Override
        public void handleEvent(SVNEvent event, double progress) throws SVNException {
            if (event.getAction() == SVNEventAction.UPDATE_UPDATE) {
                fireEvent(new SvnUpdateEvent(event.getFile()));
            } else if (event.getAction() == SVNEventAction.UPDATE_ADD) {
                fireEvent(new SvnAddEvent(event.getFile()));
            } else if (event.getAction() == SVNEventAction.UPDATE_COMPLETED) {
                fireEvent(new SvnUpdateCompletedEvent());
            } else if(event.getAction() == SVNEventAction.UPDATE_DELETE) {
                fireEvent(new SvnDeleteEvent(event.getFile()));
            } 
        }

        @Override
        public void checkCancelled() throws SVNCancelException {
        }
    }
    private SVNClientManager svn;
    private final SVNEventHandlerImpl svnEventHandlerImpl;
    private final List<ISvnActionListener> listeners = new LinkedList<ISvnActionListener>();

    public SvnTool() {

        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        svn = SVNClientManager.newInstance();
        svnEventHandlerImpl = new SVNEventHandlerImpl();
        svn.setEventHandler(svnEventHandlerImpl);
    }

    private void fireEvent(ISvnEvent event) {
        for (ISvnActionListener l : listeners) {
            l.actionPerformed(event);
        }
    }

    public void addSvnActionListener(ISvnActionListener l) {
        listeners.add(l);
    }

    public void checkout(String svnPath, File destPath) throws SVNException {
        SVNURL svnurl = SVNURL.parseURIDecoded(svnPath);
        SVNUpdateClient updateClient = svn.getUpdateClient();
        updateClient.doCheckout(svnurl, destPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
    }

    public void update(File destPath) throws SVNException {
        SVNUpdateClient updateClient = svn.getUpdateClient();
        updateClient.doUpdate(destPath, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
    }
}
