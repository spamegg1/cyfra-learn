package parprog
package blur

import org.scalameter.*
import java.awt.*, event.{ActionEvent, ActionListener}
import javax.swing.*

object ScalaShop:
  class ScalaShopFrame extends JFrame("ScalaShop"):
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    setSize(1800, 1000)
    setLayout(BorderLayout())

    val rightpanel = JPanel()
    rightpanel.setBorder(BorderFactory.createEtchedBorder(border.EtchedBorder.LOWERED))
    rightpanel.setLayout(BorderLayout())
    add(rightpanel, BorderLayout.EAST)

    val controls = JPanel()
    controls.setLayout(GridLayout(0, 2))
    rightpanel.add(controls, BorderLayout.NORTH)

    val filterLabel = JLabel("Filter")
    controls.add(filterLabel)

    val filterCombo = JComboBox(Array("horizontal-box-blur", "vertical-box-blur"))
    controls.add(filterCombo)

    val radiusLabel = JLabel("Radius")
    controls.add(radiusLabel)

    val radiusSpinner = JSpinner(SpinnerNumberModel(3, 1, 16, 1))
    controls.add(radiusSpinner)

    val tasksLabel = JLabel("Tasks")
    controls.add(tasksLabel)

    val tasksSpinner = JSpinner(SpinnerNumberModel(32, 1, 128, 1))
    controls.add(tasksSpinner)

    val stepbutton = JButton("Apply filter")
    stepbutton.addActionListener:
      new ActionListener:
        def actionPerformed(e: ActionEvent): Unit =
          val time = measure:
            canvas.applyFilter(getFilterName, getNumTasks, getRadius)
          info.setText(s"Time: ${time.value}")

    controls.add(stepbutton)

    val clearButton = JButton("Reload")
    clearButton.addActionListener:
      new ActionListener:
        def actionPerformed(e: ActionEvent): Unit = canvas.reload()

    controls.add(clearButton)

    val info = JTextArea("   ")
    info.setBorder(BorderFactory.createLoweredBevelBorder)
    rightpanel.add(info, BorderLayout.SOUTH)

    val mainMenuBar  = JMenuBar()
    val fileMenu     = JMenu("File")
    val openMenuItem = JMenuItem("Open...")
    openMenuItem.addActionListener:
      new ActionListener:
        def actionPerformed(e: ActionEvent): Unit =
          val fc = JFileChooser()
          if fc.showOpenDialog(ScalaShopFrame.this) == JFileChooser.APPROVE_OPTION
          then canvas.loadFile(fc.getSelectedFile.getPath)

    fileMenu.add(openMenuItem)

    val exitMenuItem = JMenuItem("Exit")
    exitMenuItem.addActionListener:
      new ActionListener:
        def actionPerformed(e: ActionEvent): Unit = sys.exit(0)

    fileMenu.add(exitMenuItem)
    mainMenuBar.add(fileMenu)

    val helpMenu      = JMenu("Help")
    val aboutMenuItem = JMenuItem("About")
    aboutMenuItem.addActionListener:
      new ActionListener:
        def actionPerformed(e: ActionEvent): Unit =
          JOptionPane.showMessageDialog(null, "ScalaShop")

    helpMenu.add(aboutMenuItem)
    mainMenuBar.add(helpMenu)
    setJMenuBar(mainMenuBar)
    val canvas     = PhotoCanvas()
    val scrollPane = JScrollPane(canvas)
    add(scrollPane, BorderLayout.CENTER)
    setVisible(true)

    def getNumTasks: Int      = tasksSpinner.getValue.asInstanceOf[Int]
    def getRadius: Int        = radiusSpinner.getValue.asInstanceOf[Int]
    def getFilterName: String = filterCombo.getSelectedItem.asInstanceOf[String]
  end ScalaShopFrame

  @main
  def main: Unit =
    try UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    catch
      case _: Exception =>
        println("Cannot set look and feel, using the default one.")
    val frame = ScalaShopFrame()
    frame.repaint()
