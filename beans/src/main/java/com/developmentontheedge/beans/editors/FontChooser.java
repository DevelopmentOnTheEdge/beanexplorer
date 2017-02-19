package com.developmentontheedge.beans.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * FontChooser provides a pane of controls designed to allow a user to manipulate and select a font.
 * Example of usage of FontChooser:
 * <PRE>
 *     ...
 *     Font defaultFont = new Font("Courier", Font.PLAIN, 14);
 *     Font newFont = fontChooser.showDialog(parent, "Choose new font", defaultFont);
 *     ...
 * </PRE>
 */
public class FontChooser extends JComponent
{

    /**
     * Pane used to paint <CODE>sampleText</CODE> using currently selected font.
     */
    protected SamplePanel samplePanel = new SamplePanel();

    /**
     * List of names of fonts available in the system.
     */
    protected JList<String> fontsList;

    /**
     * List of styles of fonts.
     */
    protected JList<String> stylesList;

    /**
     * List of sizes of fonts.
     */
    protected JList<String> sizesList;

    /**
     * Maps font styles to their values.
     */
    protected Map<String, Integer> styles;

    /**
     * Sample string that draw in <CODE>samplePanel</CODE>.
     */
    protected static String samlpeText = "Sample text";

    /**
     * Display names of font styles.
     */
    protected static String[] styleNames = {"Plain", "Bold", "Italic", "Bold Italic"};

    //        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
    //        "14", "16", "18", "20", "22", "24", "26", "28",
    //        "36", "48", "72",

    /**
     * Constructs default FontChooser dialog.
     */
    public FontChooser()
    {
        this(UIManager.getFont( "Menu.font" ));
    }

    /**
     * Constructs FontChooser dialog with the specified default font.
     *
     * @param font   default font for this FontChooser dialog
     */
    public FontChooser(Font font)
    {
        JPanel main = new JPanel();
        // init fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        fontsList = new JList<>(fontNames);
        fontsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontsList.addListSelectionListener(new FontChangeListener());
        JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.add(new JLabel("Font:                                                                 ",
                                 JLabel.LEFT), BorderLayout.NORTH);
        fontPanel.add(new JScrollPane(fontsList), BorderLayout.CENTER);

        // init styles
        styles = new Hashtable<>();
        styles.put(styleNames[0], Font.PLAIN);
        styles.put(styleNames[1], Font.BOLD);
        styles.put(styleNames[2], Font.ITALIC);
        styles.put(styleNames[3], Font.BOLD | Font.ITALIC);

        stylesList = new JList<>(styleNames);
        stylesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stylesList.addListSelectionListener(new StyleChangeListener());
        JPanel stylePanel = new JPanel(new BorderLayout());
        stylePanel.add(new JLabel("Style:                     ", JLabel.LEFT), BorderLayout.NORTH);
        stylePanel.add(new JScrollPane(stylesList), BorderLayout.CENTER);

        // init sizes
        Vector<String> sizes = getSizes();
        String size = String.valueOf(font.getSize());
        if (!sizes.contains(size))
        {
            sizes.add(size);
        }

        sizesList = new JList<>(sizes);
        sizesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizesList.addListSelectionListener(new SizeChangeListener());
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.add(new JLabel("Size:          ", JLabel.LEFT), BorderLayout.NORTH);
        sizePanel.add(new JScrollPane(sizesList), BorderLayout.CENTER);

        // init main panels
        main.add(fontPanel);
        main.add(stylePanel);
        main.add(sizePanel);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(main, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        centerPanel.add(samplePanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);


        fontsList.setSelectedValue(font.getFamily(), true);
        sizesList.setSelectedValue(size, true);

        switch ( font.getStyle() )
        {
            case Font.PLAIN :
                stylesList.setSelectedValue(styleNames[0], true);
                break;
            case Font.BOLD :
                stylesList.setSelectedValue(styleNames[1], true);
                break;
            case Font.ITALIC :
                stylesList.setSelectedValue(styleNames[2], true);
                break;
            case Font.BOLD + Font.ITALIC :
                stylesList.setSelectedValue(styleNames[3], true);
                break;
        }
    }


    /**
     * Return currently selected font.
     *
     * @return current font
     */
    @Override
    public Font getFont()
    {
        String name = fontsList.getSelectedValue();
        int style = styles.get(stylesList.getSelectedValue()).intValue();
        int size = Integer.parseInt(sizesList.getSelectedValue());

        return new Font(name, style, size);
    }


    /**
     * Display modal FontChooser dialog that permit user to select new font.
     *
     * @param parent parent component of the FontChooser dialog
     * @param title  title of the FontChooser dialog
     * @param font   font that will be initialy displayed in FontChooser dialog (default font)
     * @return font that was selected by user
     */
    static public Font showDialog(Component parent, String title, Font font)
    {
        if ( title == null )
        {
            title = "Select Font";
        }

        FontChooser chooserPane = new FontChooser(font);
        FontTracker fontTracker = new FontTracker(chooserPane);

        FontChooserDialog dialog = new FontChooserDialog(parent, title, true, chooserPane,
                                                         fontTracker, null);

        dialog.addWindowListener(new WindowAdapter()
                                 {
                                     @Override
                                    public void windowClosing(WindowEvent e)
                                     {
                                         Window w = e.getWindow();
                                         w.setVisible(false);
                                     }
                                 });

        dialog.addComponentListener(new ComponentAdapter()
                                    {
                                        @Override
                                        public void componentHidden(ComponentEvent e)
                                        {
                                            Window w = (Window)e.getComponent();
                                            w.dispose();
                                        }
                                    });

        Dimension screenSize = dialog.getToolkit().getScreenSize();
        dialog.setLocation((screenSize.width - dialog.getSize().width)/2, (screenSize.height - dialog.getSize().height)/2);

        dialog.pack();
        dialog.setVisible(true);

        return fontTracker.getFont() == null ? font : fontTracker.getFont();
    }

    private Vector<String> getSizes()
    {
        Vector<String> sizes = new Vector<>();
        int i=0;
        while (i<13)
        {
            sizes.add(String.valueOf(i));
            i++;
        }
        i=14;
        while (i<29)
        {
            sizes.add(String.valueOf(i));
            i+=2;
        }
        sizes.add("36");
        sizes.add("48");
        sizes.add("72");

        return sizes;
    }

    class FontChangeListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            samplePanel.repaint();
        }
    }

    class StyleChangeListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            samplePanel.repaint();
        }
    }

    class SizeChangeListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            samplePanel.repaint();
        }
    }

    class SamplePanel extends JComponent
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g)
        {
            Graphics2D g2 = (Graphics2D)g;
            RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHints(hints);

            g2.setFont(FontChooser.this.getFont());

            FontMetrics fm = g2.getFontMetrics();

            Rectangle rect = getVisibleRect();
            double x = rect.getCenterX() - fm.stringWidth(samlpeText)/2.0;
            double y = rect.getCenterY() + fm.getHeight()/3.0;

            g2.drawString(samlpeText, (int)x, (int)y);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(300, 100);
        }
    }
}

/*
*
* Class which builds a font dialog consisting of
* a JColorChooser with "Ok" and "Cancel" buttons.
*
* Note: This needs to be fixed to deal with localization!
*/
class FontChooserDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    
    private Font initialFont;
    private final FontChooser chooserPane;

    public FontChooserDialog(Component c, String title, boolean modal,
                             FontChooser chooserPane,
                             ActionListener okListener,
                             ActionListener cancelListener)
    {
        super(JOptionPane.getFrameForComponent(c), title, modal);
        //setResizable(false);

        this.chooserPane = chooserPane;

        String okString = UIManager.getString("ColorChooser.okText");
        String cancelString = UIManager.getString("ColorChooser.cancelText");

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(chooserPane, BorderLayout.CENTER);

        /*
         * Create Lower button panel
         */
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton okButton = new JButton(okString);
        if (okListener != null)
        {
            okButton.addActionListener(okListener);
        }
        okButton.addActionListener(new ActionListener()
                                   {
                                       @Override
                                    public void actionPerformed(ActionEvent e)
                                       {
                                           setVisible(false);
                                       }
                                   });
        buttonPane.add(okButton);

        JButton cancelButton = new JButton(cancelString);
        if (cancelListener != null)
        {
            cancelButton.addActionListener(cancelListener);
        }
        cancelButton.addActionListener(new ActionListener()
                                       {
                                           @Override
                                        public void actionPerformed(ActionEvent e)
                                           {
                                               setVisible(false);
                                           }
                                       });
        buttonPane.add(cancelButton);

        getRootPane().setDefaultButton(okButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(c);
    }
}

class FontTracker implements ActionListener
{
    FontChooser chooser;
    Font font;

    public FontTracker(FontChooser f)
    {
        chooser = f;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        font = chooser.getFont();
    }

    public Font getFont()
    {
        return font;
    }
}
