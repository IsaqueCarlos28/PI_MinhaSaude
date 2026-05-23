package com.example.medicoapplication.data.remote.DTO.consulta

// FIX: StatusConsulta was duplicated here AND in the root DTO package.
// Having two classes with the same name in different packages causes
// "Unresolved reference" ambiguity in files that import both.
// Removed the duplicate definition — use the root DTO.StatusConsulta everywhere.
// This file is kept only as a documented redirect.

// Import the single source of truth:
typealias StatusConsulta = com.example.medicoapplication.data.remote.DTO.StatusConsulta
