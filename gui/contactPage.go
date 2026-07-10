package gui

import (
	"fmt"
	"net/url"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
)

func initContact() fyne.CanvasObject {
	title := title("Contact:")

	txt := fmt.Sprintf(
		"If you have any bugs, improvements or issues,\n" +
		"You can contact me through the email.\n" +
		"PLEASE BE CAUTIOUS, this is considered as a cheat\nin some servers and may get you BANNED.",
	)

	url, _ := url.Parse("mailto:imlrand12@gmail.com")
	email := widget.NewHyperlink("imlrand12@gmail.com", url)

	c := container.NewPadded(container.New(layout.NewVBoxLayout(), title, widget.NewLabel(txt), email))
	return c
}
