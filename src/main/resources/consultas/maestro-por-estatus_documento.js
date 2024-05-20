let db = {
    escuela: {
        aggregate: function (query) {}
    }
};

db.escuela.aggregate([
    {
        $match: { "_class": "unach.sindicato.api.persistence.escuela.Maestro" }
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
        $match: { "documentos.reporte.motivo": "ACEPTADO" }
    },
    {
        $project: { "documentos.bytes": 0 }
    }
])