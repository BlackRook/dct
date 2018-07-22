package ru.domclicktest.dct.models

import javax.persistence.*

@Entity
@Table(name = "accounts")
data class Account(
        var name:String="",
        var balance:Double=0.0,
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id:Long=0)