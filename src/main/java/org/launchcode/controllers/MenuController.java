package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;
    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value="")
    public String index(Model model) {
        model.addAttribute("title", "All Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String addDisplay(Model model) {
        model.addAttribute("title", "Add New Menu");
        model.addAttribute("menu", new Menu());

        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String addProcess(Model model, @ModelAttribute @Valid Menu newMenu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add New Menu");
            return "menu/add";
        }
        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){

        Menu queriedMenu = menuDao.findOne(menuId);
        model.addAttribute("menu", queriedMenu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu queriedMenu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(queriedMenu, cheeseDao.findAll());

        model.addAttribute("title", "Add Item to: " + queriedMenu.getName());
        model.addAttribute("form", form);

        return "menu/add-item";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @PathVariable int menuId,
                          @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if (errors.hasErrors()) {
            model.addAttribute("title", form.getMenu().getName());
            return "redirect:/add-item/" + menuId;
        }

        Cheese addedCheese = cheeseDao.findOne(form.getCheeseId());
        Menu targetMenu = menuDao.findOne(form.getMenuId());
        targetMenu.addItem(addedCheese);
        menuDao.save(targetMenu);

        return "redirect:/menu/view/" + menuId;

    }

}
