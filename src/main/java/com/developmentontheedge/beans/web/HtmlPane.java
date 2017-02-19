// $Id: HtmlPane.java,v 1.7 2007/10/04 07:02:38 adolg Exp $
package com.developmentontheedge.beans.web;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.developmentontheedge.beans.web.resources.MessageBundle;

/**
 * @pending OpenInBrowser action
 */
public class HtmlPane extends JPanel implements HyperlinkListener
{
    protected JEditorPane editor;
    protected JScrollPane scrollPane;
    protected ArrayList<Object> hyperlinkList;
    protected int curListIndex;

    public HtmlPane()
    {
        hyperlinkList = new ArrayList<>();
        initActions();

        editor = new JEditorPane("text/html", "")
        {
            // workaround if some invalid text
            // that can not be shown correctly
            @Override
            public void doLayout()
            {
                try
                {
                    super.doLayout();
                }
                catch(Throwable t)
                {}
            }
        };

        editor.addHyperlinkListener(this);
        editor.setEditable(false);

        scrollPane = new JScrollPane(editor);
 
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, scrollPane);
    }

    public void setInitialText(Object data)
    {
        setText(data);

        hyperlinkList.clear();
        hyperlinkList.add(data);
        curListIndex = 0;

        forwardAction.setEnabled(false);
        backwardAction.setEnabled(false);
    }

    protected void setText(Object data)
    {
        if( data instanceof String )
        {
            editor.setText((String)data);
            editor.setCaretPosition(0);
        }
        else if( data instanceof URL )
        {
            URL url = (URL)data;
            try
            {
                editor.setPage( (URL)data );
            }
            catch(Throwable t)
            {
                String text = "Invalid link: " + url + "<br>Error: " + t;
                editor.setText(text);
            }
        }
        else
        {
            editor.setText("Data=" + data);
            editor.setCaretPosition(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties
    //

    public Action getForwardAction()
    {
        return forwardAction;
    }
    public boolean isForwardActionEnabled()
    {
        return curListIndex < hyperlinkList.size() - 1;
    }

    public Action getBackwardAction()
    {
        return backwardAction;
    }
    public boolean isBackwardActionEnabled()
    {
        return curListIndex > 0;
    }

    public JEditorPane getEditor()
    {
        return editor;
    }

    public Action[] getActions()
    {
        return actions;
    }

    ////////////////////////////////////////////////////////////////////////////
    // HyperlinkListener interface implementations
    //

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            if (e.getURL() != null)
            {
                followHyperlink( e.getURL() );
            }
        }
    }

    protected void followHyperlink(Object data)
    {
        if (curListIndex + 1 < hyperlinkList.size())
        {
            for(int i=hyperlinkList.size()-1; i>curListIndex; i--)
            hyperlinkList.remove(i);
        }

        setText(data);
        hyperlinkList.add(data);
        curListIndex++;

        backwardAction.setEnabled( isBackwardActionEnabled() );
        forwardAction.setEnabled( isForwardActionEnabled() );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Action issues
    //

    public static final String FORWARD_ACTION = "FORWARD_ACTION";
    public static final String BACKWARD_ACTION = "BACKWARD_ACTION";

    protected Action[] actions;
    protected Action forwardAction;
    protected Action backwardAction;

    protected void initActions()
    {
        backwardAction = new BackwardAction(BACKWARD_ACTION);
        forwardAction = new ForwardAction(FORWARD_ACTION);

        ActionInitializer.init(MessageBundle.class.getName(), MessageBundle.class);
        ActionInitializer.initAction(backwardAction, BACKWARD_ACTION);
        ActionInitializer.initAction(forwardAction, FORWARD_ACTION);

        actions = new Action[] { backwardAction, forwardAction };
        for (int i = 0; i < actions.length; i++)
        {
            Action action = actions[i];
            action.setEnabled(false);
        }
    }

    class BackwardAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public BackwardAction(String name)
        {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (curListIndex > 0)
            {
                curListIndex--;
                setText(hyperlinkList.get(curListIndex));
                forwardAction.setEnabled(true);
            }
            backwardAction.setEnabled(curListIndex > 0);
        }
    }


    class ForwardAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        public ForwardAction(String name)
        {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (curListIndex < hyperlinkList.size() - 1)
            {
                curListIndex++;
                setText(hyperlinkList.get(curListIndex));
                backwardAction.setEnabled(true);
            }
            forwardAction.setEnabled(curListIndex < hyperlinkList.size() - 1);
        }
    }


    /*
    class OpenInBrowserAction extends AbstractAction
    {
        public OpenInBrowserAction( String name )
        {
            super( name );
        }

        public void actionPerformed( ActionEvent e )
        {

        }
    }
    */
}
