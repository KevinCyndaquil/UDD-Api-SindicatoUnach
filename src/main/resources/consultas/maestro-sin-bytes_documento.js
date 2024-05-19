db.escuela.aggregate([
    {
        $match: {
            "correo_institucional.direccion": "erwin.bermudez@unach.mx",
            "_class": "unach.sindicato.api.persistence.escuela.Maestro" }
    },
    {
        $lookup: {
            from: "documentos",
            localField: "documentos.$id",
            foreignField: "_id",
            as: "documentos"
        }
    },
    {
        $project: { "documentos.bytes": 0 }
    }
])