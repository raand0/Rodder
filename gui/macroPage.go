package gui

import (
	"MacroGo/config"
	"MacroGo/core"
	"MacroGo/shared"
	"image/color"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"

	"fyne.io/fyne/v2/widget"
)

func InitMacro() {
	myApp := app.New()
	myWindow := myApp.NewWindow("Rodder")
	shared.App = myApp

	//row gap
	rowGap := canvas.NewRectangle(color.Transparent)
	rowGap.SetMinSize(fyne.NewSize(0, 15))

	//first row
	swordContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Sword", &config.SwordKey))
	rodContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Rod", &config.RodKey))
	firstRow := container.New(layout.NewHBoxLayout(), swordContainer, layout.NewSpacer(), rodContainer)

	//macro keybind
	var macroSelect *widget.Select
	macroSelect = widget.NewSelect(selectOptions(), func(option string) {
		err := shared.CheckBinds(option, 3)
		if(err != nil){
			macroSelect.ClearSelected()
			config.MacroCode = 0
		}else{
			config.MacroKey = option
			for key, value := range config.SpecialKeys {
				if value == config.MacroKey {
					config.MacroCode = key
					break
				}
			}
		}
	})

	//toggle keybing
	var toggleSelect *widget.Select
	toggleSelect = widget.NewSelect(selectOptions(), func(option string) {
		err := shared.CheckBinds(option, 4)
		if(err != nil){
			toggleSelect.ClearSelected()
			config.ToggleCode = 0
		}else{
			config.ToggleKey = option
			for key, value := range config.SpecialKeys {
				if value == config.ToggleKey {
					config.ToggleCode = key
					break
				}
			}
		}
	})

	//second row
	macroContainer := container.New(layout.NewHBoxLayout(), widget.NewLabel("Macro"), layout.NewSpacer(), macroSelect)
	toggleContainer := container.New(layout.NewHBoxLayout(), widget.NewLabel("Toggle"), toggleSelect)
	secondRow := container.New(layout.NewHBoxLayout(), macroContainer, layout.NewSpacer(), toggleContainer)

	//checkbox
	swordCheck := widget.NewCheck("Back to sword", func(checked bool) {
		config.BackToSword = checked
	})
	thirdRow := container.New(layout.NewCenterLayout(), swordCheck)

	//pack everything
	macroPage := container.New(layout.NewVBoxLayout(), firstRow, rowGap, secondRow, rowGap, thirdRow)
	padded := container.NewPadded(macroPage)

	//header
	head := canvas.NewText("Rodder", color.RGBA{R: 25, G: 118, B: 210, A: 255})
	head.TextSize = 20
	head.TextStyle = fyne.TextStyle{Bold: true}
	header := container.NewPadded(container.NewHBox(head, layout.NewSpacer()))

	//switch button
	TglBtn := widget.NewButton("OFF", shared.SwitchHandler)
	TglBtn.Importance = widget.DangerImportance
	shared.SharedBtn = TglBtn

	//footer
	creator := canvas.NewText("rand", color.RGBA{R: 25, G: 118, B: 210, A: 255})
	creator.TextSize = 12
	version := canvas.NewText("version 1.0", color.RGBA{R: 25, G: 118, B: 210, A: 255})
	version.TextSize = 12

	footer := container.NewHBox(creator, layout.NewSpacer(), version)
	footerWithButton := container.NewPadded(container.NewVBox(container.NewCenter(TglBtn), footer))

	//navigation
	tabs := container.NewAppTabs(
		container.NewTabItem("Macro", padded),
		container.NewTabItem("How to use", initHow()),
		container.NewTabItem("Contact", initContact()),
	)

	tabs.SetTabLocation(container.TabLocationLeading)

	final := container.New(layout.NewBorderLayout(header, footerWithButton, nil, nil), header, footerWithButton, tabs)

	go core.Macro()
	myWindow.CenterOnScreen()
	myWindow.SetContent(final)
	myWindow.Resize(fyne.NewSize(400, 400))
	myWindow.SetFixedSize(true)
	myWindow.ShowAndRun()
}

func createPair(app fyne.App, label string, trackingKey *string) fyne.CanvasObject {
	lbl := widget.NewLabel(label)

	var btn *widget.Button
	btn = widget.NewButton("Select", func() {

		selectWin := app.NewWindow("press a key")
		container := container.New(layout.NewCenterLayout(), widget.NewLabel("PRESS A KEY"))
		selectWin.SetContent(container)
		selectWin.Resize(fyne.NewSize(50, 50))
		selectWin.Show()

		go func() {
			key := core.Listen()
			if(label == "Sword"){
				err := shared.CheckBinds(key, 1)
				if(err != nil){
					key = "None"
				}
			}else{
				err := shared.CheckBinds(key, 2)
				if(err != nil){
					key = "None"
				}
			}

			*trackingKey = key
			fyne.Do(func() {
				btn.SetText(*trackingKey)
			})

			fyne.Do(func() {
				selectWin.Close()
			})
		}()
	})

	return container.New(layout.NewHBoxLayout(), lbl, layout.NewSpacer(), btn)
}

func selectOptions() []string {
	return []string{"tab", "`", "capslock", "lshift", "lctrl", "lalt", "space", "backspace", "[", "]", "\\", "enter", "/", "rshift", "ralt", "printscreen", "rctrl"}
}
