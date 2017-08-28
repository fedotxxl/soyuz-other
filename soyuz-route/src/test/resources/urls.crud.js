var vRoutes = {
    auth: {
        login: "/auth/login.html",
        invite: {
            confirm: "/auth/invite.confirm.html"
        },
        password: {
            reset:  "/auth/password.reset.html"
        }
    },
    app: {
        root: "/app/",
        campaigns: {
            list: "/app/campaigns/",
            item: {
                create: "/app/campaigns/new",
                show: "/app/campaigns/:id",
                edit: "/app/campaigns/:id/edit",
            }
        },
        settings: {
            users: {
                list: "/app/settings/users/",
                item: {
                    create: "/app/settings/users/new",
                    edit: "/app/settings/users/:id"
                }
            }
        }
    }
};