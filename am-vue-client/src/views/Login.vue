<template>
    <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="user">
            <i-Input type="text" v-model="formInline.user" placeholder="Username">
                <Icon type="ios-person-outline" slot="prepend"></Icon>
            </i-Input>
        </FormItem>
        <FormItem prop="password">
            <i-Input @on-enter="handleSubmit('formInline')" type="password" v-model="formInline.password" placeholder="Password">
                <Icon type="ios-lock-outline" slot="prepend"></Icon>
            </i-Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="handleSubmit('formInline')">Sign in</Button>
        </FormItem>
    </Form>
</template>


<script>

import { login } from '@/js/auth.js';
import { VIEW_NAME_ABOUT } from '@/js/constants.js';

export default {

    name: 'login',

    data () {
        return {
            formInline: {
                user: '',
                password: ''
            },
            ruleInline: {
                user: [
                    { required: true, message: 'Please fill in the user name', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: 'Please fill in the password.', trigger: 'blur' }
                ]
            }
        }
    },

    methods: {

        handleSubmit(name) {

            var username = this.formInline.user;
            var password = this.formInline.password;

            var router = this.$router;
            var onSuccess = function() { router.push({ name: VIEW_NAME_ABOUT }); };

            var msgAlerter = this.$Message;
            var onFailure = function() { msgAlerter.error('Authentication Failure'); };

            this.$refs[name].validate((valid) => {
                if (valid) {
                    login(username, password, onSuccess, onFailure);
                } else {
                    this.$Message.error('Form fields are not valid');
                }
            });

        },

        onEnter: function() {
            console.log("Enter key");
        }


    }

}

</script>
