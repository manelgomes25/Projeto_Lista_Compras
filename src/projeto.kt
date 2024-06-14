import javax.swing.*
import javax.swing.border.EmptyBorder
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

data class Item(var name: String, var quantity: Int, var notes: String = "", var purchased: Boolean = false)

class ShoppingList(val name: String) {
    val items = mutableListOf<Item>()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(itemName: String) {
        items.removeIf { it.name == itemName }
    }

    fun editItem(oldItemName: String, newItem: Item) {
        val itemIndex = items.indexOfFirst { it.name == oldItemName }
        if (itemIndex != -1) {
            items[itemIndex] = newItem
        }
    }

    fun markItemAsPurchased(itemName: String) {
        val item = items.find { it.name == itemName }
        item?.purchased = true
    }

    fun listItems(): String {
        return if (items.isEmpty()) {
            "A lista está vazia."
        } else {
            items.joinToString(separator = "\n") {
                "${it.name} - Quantidade: ${it.quantity}, Notas: ${it.notes}, Comprado: ${if (it.purchased) "Sim" else "Não"}"
            }
        }
    }
}

class ShoppingApp {
    val lists = mutableMapOf<String, ShoppingList>()

    fun createList(listName: String) {
        lists[listName] = ShoppingList(listName)
    }

    fun getList(listName: String): ShoppingList? {
        return lists[listName]
    }

    fun removeList(listName: String) {
        lists.remove(listName)
    }
}

class ShoppingAppGUI : JFrame("Aplicação de Lista de Compras"), ActionListener {
    private val app = ShoppingApp()
    private val listModel = DefaultListModel<String>()
    private val itemListModel = DefaultListModel<String>()
    private val listList = JList(listModel)
    private val itemList = JList(itemListModel)

    init {
        layout = BorderLayout(10, 10)

        // Configurações gerais da janela
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(800, 600)
        isVisible = true
        contentPane.background = Color(240, 240, 240)

        // Painel de Listas
        val listPanel = JPanel(BorderLayout(10, 10))
        listPanel.border = EmptyBorder(10, 10, 10, 10)
        listPanel.background = Color(255, 255, 255)

        val listTitle = JLabel("Listas de Compras")
        listTitle.font = Font("Arial", Font.BOLD, 16)
        listPanel.add(listTitle, BorderLayout.NORTH)

        listList.fixedCellHeight = 30
        listList.border = BorderFactory.createLineBorder(Color(200, 200, 200))
        listPanel.add(JScrollPane(listList), BorderLayout.CENTER)

        val listButtonPanel = JPanel(GridLayout(1, 2, 10, 10))
        listButtonPanel.background = Color(255, 255, 255)
        val addListButton = JButton("Adicionar Lista")
        val removeListButton = JButton("Remover Lista")
        addListButton.background = Color(0, 123, 255)
        addListButton.foreground = Color.WHITE
        removeListButton.background = Color(220, 53, 69)
        removeListButton.foreground = Color.WHITE
        listButtonPanel.add(addListButton)
        listButtonPanel.add(removeListButton)
        listPanel.add(listButtonPanel, BorderLayout.SOUTH)

        // Painel de Itens
        val itemPanel = JPanel(BorderLayout(10, 10))
        itemPanel.border = EmptyBorder(10, 10, 10, 10)
        itemPanel.background = Color(255, 255, 255)

        val itemTitle = JLabel("Itens da Lista")
        itemTitle.font = Font("Arial", Font.BOLD, 16)
        itemPanel.add(itemTitle, BorderLayout.NORTH)

        itemList.fixedCellHeight = 30
        itemList.border = BorderFactory.createLineBorder(Color(200, 200, 200))
        itemPanel.add(JScrollPane(itemList), BorderLayout.CENTER)

        val itemButtonPanel = JPanel(GridLayout(1, 4, 10, 10))
        itemButtonPanel.background = Color(255, 255, 255)
        val addItemButton = JButton("Adicionar Item")
        val editItemButton = JButton("Editar Item")
        val removeItemButton = JButton("Remover Item")
        val markItemButton = JButton("Marcar como Comprado")
        addItemButton.background = Color(40, 167, 69)
        addItemButton.foreground = Color.WHITE
        editItemButton.background = Color(255, 193, 7)
        editItemButton.foreground = Color.WHITE
        removeItemButton.background = Color(220, 53, 69)
        removeItemButton.foreground = Color.WHITE
        markItemButton.background = Color(0, 123, 255)
        markItemButton.foreground = Color.WHITE
        itemButtonPanel.add(addItemButton)
        itemButtonPanel.add(editItemButton)
        itemButtonPanel.add(removeItemButton)
        itemButtonPanel.add(markItemButton)
        itemPanel.add(itemButtonPanel, BorderLayout.SOUTH)

        // Adicionar os painéis à janela principal
        add(listPanel, BorderLayout.WEST)
        add(itemPanel, BorderLayout.CENTER)

        // Adicionar listeners aos botões
        addListButton.addActionListener {
            val listName = JOptionPane.showInputDialog(this, "Digite o nome da lista:")
            if (listName != null && listName.isNotBlank()) {
                app.createList(listName)
                listModel.addElement(listName)
            }
        }

        removeListButton.addActionListener {
            val selectedList = listList.selectedValue
            if (selectedList != null) {
                app.removeList(selectedList)
                listModel.removeElement(selectedList)
                itemListModel.clear()
            }
        }

        listList.addListSelectionListener {
            val selectedList = listList.selectedValue
            if (selectedList != null) {
                val list = app.getList(selectedList)
                if (list != null) {
                    itemListModel.clear()
                    list.items.forEach {
                        itemListModel.addElement("${it.name} - Quantidade: ${it.quantity}, Notas: ${it.notes}, Comprado: ${if (it.purchased) "Sim" else "Não"}")
                    }
                }
            }
        }

        addItemButton.addActionListener {
            val selectedList = listList.selectedValue
            if (selectedList != null) {
                val list = app.getList(selectedList)
                if (list != null) {
                    val itemName = JOptionPane.showInputDialog(this, "Digite o nome do item:")
                    val itemQuantity = JOptionPane.showInputDialog(this, "Digite a quantidade:").toInt()
                    val itemNotes = JOptionPane.showInputDialog(this, "Digite notas adicionais (opcional):")
                    val item = Item(itemName, itemQuantity, itemNotes ?: "")
                    list.addItem(item)
                    itemListModel.addElement("${item.name} - Quantidade: ${item.quantity}, Notas: ${item.notes}, Comprado: ${if (item.purchased) "Sim" else "Não"}")
                }
            }
        }

        editItemButton.addActionListener {
            val selectedList = listList.selectedValue
            val selectedItemIndex = itemList.selectedIndex
            if (selectedList != null && selectedItemIndex != -1) {
                val list = app.getList(selectedList)
                if (list != null) {
                    val oldItem = list.items[selectedItemIndex]
                    val itemName = JOptionPane.showInputDialog(this, "Digite o novo nome do item:", oldItem.name)
                    val itemQuantity = JOptionPane.showInputDialog(this, "Digite a nova quantidade:", oldItem.quantity.toString()).toInt()
                    val itemNotes = JOptionPane.showInputDialog(this, "Digite novas notas adicionais (opcional):", oldItem.notes)
                    val newItem = Item(itemName, itemQuantity, itemNotes ?: "")
                    list.editItem(oldItem.name, newItem)
                    itemListModel.set(selectedItemIndex, "${newItem.name} - Quantidade: ${newItem.quantity}, Notas: ${newItem.notes}, Comprado: ${if (newItem.purchased) "Sim" else "Não"}")
                }
            }
        }

        removeItemButton.addActionListener {
            val selectedList = listList.selectedValue
            val selectedItemIndex = itemList.selectedIndex
            if (selectedList != null && selectedItemIndex != -1) {
                val list = app.getList(selectedList)
                if (list != null) {
                    val itemName = list.items[selectedItemIndex].name
                    list.removeItem(itemName)
                    itemListModel.remove(selectedItemIndex)
                }
            }
        }

        markItemButton.addActionListener {
            val selectedList = listList.selectedValue
            val selectedItemIndex = itemList.selectedIndex
            if (selectedList != null && selectedItemIndex != -1) {
                val list = app.getList(selectedList)
                if (list != null) {
                    val item = list.items[selectedItemIndex]
                    list.markItemAsPurchased(item.name)
                    itemListModel.set(selectedItemIndex, "${item.name} - Quantidade: ${item.quantity}, Notas: ${item.notes}, Comprado: Sim")
                }
            }
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        // Handle action events if necessary
    }
}

fun main() {
    SwingUtilities.invokeLater {
        ShoppingAppGUI()
    }
}
