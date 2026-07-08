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

var MacroKey string
var ToggleKey string

func InitializeMacro() {
	myApp := app.New()
	myWindow := myApp.NewWindow("Rodder")

	//testing labels
	t_s := widget.NewLabel("swordkey is " + core.SwordKey)
	t_r := widget.NewLabel("rodkey is " + core.RodKey)
	t_m := widget.NewLabel("macrokey is " + MacroKey)
	t_t := widget.NewLabel("togglekey is " + ToggleKey)

	//row padding
	rowGap := canvas.NewRectangle(color.Transparent)
	rowGap.SetMinSize(fyne.NewSize(0, 15))

	//first row
	swordContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Sword", &core.SwordKey, t_s))
	rodContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Rod", &core.RodKey, t_r))
	firstRow := container.New(layout.NewHBoxLayout(), swordContainer, layout.NewSpacer(), rodContainer)

	//second row
	macroSelect := widget.NewSelect(selectOptions(), func(option string) {
		MacroKey = option
		t_m.SetText("macrokey is " + MacroKey)
		for key, value := range config.SpecialKeys {
			if value == MacroKey {
				core.MacroCode = key
				break
			}
		}
	})
	toggleSelect := widget.NewSelect(selectOptions(), func(option string) {
		ToggleKey = option
		t_m.SetText("togglekey is " + ToggleKey)
		for key, value := range config.SpecialKeys {
			if value == ToggleKey {
				core.ToggleCode = key
				break
			}
		}
	})

	macroContainer := container.New(layout.NewHBoxLayout(), widget.NewLabel("Macro"), layout.NewSpacer(), macroSelect)
	toggleContainer := container.New(layout.NewHBoxLayout(), widget.NewLabel("Toggle"), toggleSelect)
	secondRow := container.New(layout.NewHBoxLayout(), macroContainer, layout.NewSpacer(), toggleContainer)

	//checkbox
	swordCheck := widget.NewCheck("Back to sword", func(checked bool) {
		core.BackToSword = checked
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

	//other page
	c := container.New(layout.NewHBoxLayout(), t_s, t_r, t_m, t_t)

	tabs := container.NewAppTabs(
		container.NewTabItem("Macro", padded),
		container.NewTabItem("How to use", c),
	)

	tabs.SetTabLocation(container.TabLocationLeading)

	final := container.New(layout.NewBorderLayout(header, footerWithButton, nil, nil), header, footerWithButton, tabs)

	go core.Macro()
	myWindow.SetContent(final)
	myWindow.Resize(fyne.NewSize(400, 400))
	myWindow.ShowAndRun()
}

func createPair(app fyne.App, label string, trackingKey *string, trackingLabel *widget.Label) fyne.CanvasObject {
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

			*trackingKey = key
			fyne.Do(func() {
				trackingLabel.SetText("swordkey is " + *trackingKey)
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
