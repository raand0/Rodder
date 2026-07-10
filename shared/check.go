package shared

import (
	"MacroGo/config"
	"errors"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

var App fyne.App

//stfu i know this is ugly
func CheckBinds(bind string, i byte) error{

	if(bind == config.SwordKey && i != 1){
		showError()
		return errors.New("Overlap")
	}else if(bind == config.RodKey && i != 2){
		showError()
		return errors.New("Overlap")
	}else if(bind == config.MacroKey && i != 3){
		showError()
		return errors.New("Overlap")
	}else if(bind == config.ToggleKey && i != 4){
		showError()
		return errors.New("Overlap")
	}

	return nil
}

func showError(){
	fyne.Do(func(){
		win := App.NewWindow("error")
		win.SetContent(container.NewHBox(widget.NewLabel("Equal keybinds are not allowed!")))
		win.Show()
	})
}
