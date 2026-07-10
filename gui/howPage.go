package gui

import (
	"fmt"
	"image/color"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
)

func initHow() fyne.CanvasObject {
	title := title("How to use:")

	txt := fmt.Sprintf(
		"Sword: Your sword keybind.\n" +
		"Rod: Your fishing rod keybind.\n" +
		"Macro: The macro keybind which executes the macro.\n" +
		"Toggle: Toggle key is the key used to enable and\ndisable the app.(toggles the ON/OFF switch)\n" +
		"Back to sword: Wether to press the sword key finally.",
	)

	c := container.NewPadded(container.New(layout.NewVBoxLayout(), title, widget.NewLabel(txt)))

	return c
}

func title(text string) *canvas.Text {
	Title := canvas.NewText(text, color.White)
	Title.TextSize = 20
	return Title
}
