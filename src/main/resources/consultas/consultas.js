db.escuela.aggregate([
    {
        $lookup: {
            from: "documentos",
            localField: "documentos.$id",
            foreignField: "_id",
            as: "documentos"
        }
    },
    {
        $unwind: "$documentos",
    },
    {
        $project: {
            "documentos.bytes": 0
        }
    }
])