package file

import (
	"fmt"
	"os"
	"path/filepath"
	"encoding/json"
	"Rodder/config"
)

type Binds struct{
	SwordKey string `json:"SwordKey"`
	RodKey string `json:"RodKey"`
	BackToSword bool `json:"BackToSword"`
	ToggleCode uint16 `json:"ToggleCode"`
	MacroCode uint16 `json:"MacroCode"`
	MacroKey string `json:"MacroKey"`
	ToggleKey string `json:"ToggleKey"`
}

var userConfigDir = getConfigDir()
var configDir = filepath.Join(userConfigDir, "Rodder")
var keybindFile = filepath.Join(configDir, "keybinds.json")

func getConfigDir() string{
	dir, err := os.UserConfigDir()
	if(err != nil){
		fmt.Println(err)
		return ""
	}

	return dir
}

func createFile(){

	err := os.MkdirAll(configDir, 0755)
	if(err != nil){
		fmt.Println(err)
		return
	}

	_, err = os.Create(keybindFile)
	if(err != nil){
		fmt.Println(err)
		return
	}
}

func Write(){

	binds := Binds{
		SwordKey: config.SwordKey,
		RodKey: config.RodKey,
		BackToSword: config.BackToSword,
		ToggleCode: config.ToggleCode,
		MacroCode: config.MacroCode,
		MacroKey: config.MacroKey,
		ToggleKey: config.ToggleKey,
	}

	bytes, err := json.MarshalIndent(binds, "", "	")
	if(err != nil){
		fmt.Println(err)
		return
	}

	err = os.WriteFile(keybindFile, bytes, 0644)
	if(err != nil){
		fmt.Println(err)
		return
	}

}
