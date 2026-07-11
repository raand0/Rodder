package file

import (
	"encoding/json"
	"os"
	"fmt"
	"errors"
	"MacroGo/config"
)

func Read(){

	_, err := os.Stat(keybindFile)
	if(err != nil || errors.Is(err, os.ErrNotExist)){
		createFile()
		return
	}

	bytes, err := os.ReadFile(keybindFile)
	if(err != nil){
		fmt.Println(err)
		return
	}

	var binds Binds
	err = json.Unmarshal(bytes, &binds)
	if(err != nil){
		fmt.Println(err)
		return
	}

	assignValues(&binds)
}

func assignValues(binds *Binds){

	config.SwordKey = binds.SwordKey
	config.RodKey = binds.RodKey
	config.BackToSword = binds.BackToSword
	config.ToggleCode = binds.ToggleCode
	config.MacroCode = binds.MacroCode
	config.MacroKey = binds.MacroKey
	config.ToggleKey = binds.ToggleKey
}
