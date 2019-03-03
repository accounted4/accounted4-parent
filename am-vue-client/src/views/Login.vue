<template>
    <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="user">
            <i-Input type="text" v-model="formInline.user" placeholder="Username">
                <Icon type="ios-person-outline" slot="prepend"></Icon>
            </i-Input>
        </FormItem>
        <FormItem prop="password">
            <i-Input type="password" v-model="formInline.password" placeholder="Password">
                <Icon type="ios-lock-outline" slot="prepend"></Icon>
            </i-Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="handleSubmit('formInline')">Signin</Button>
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
            this.$refs[name].validate((valid) => {
                if (valid) {
                    login();
                    this.$router.push({ name: VIEW_NAME_ABOUT });
                } else {
                    this.$Message.error('Authentication Failure');
                }
            })
        }
    }

}

</script>
