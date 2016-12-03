/*



/p - projects list
/p/:id/ - exceptions list
/p/:id/settings - settings
/u - user settings







 */

var vRoutes = {
    projects: {
        list: "/p/",
        settings: "/p/:projectId/settings"
    },
    exceptions: {
        list: "/p/:projectId/"
    },
    user: {
        settings: "/u/"
    }
};

// v-href="{path: "projects.list", projectId: 123, v: v}"