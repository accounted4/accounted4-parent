<template >
    <div  class="center-container">
    <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="user">
            <Input type="text" v-model="formInline.user" placeholder="Username" autofocus>
                <Icon type="ios-person-outline" slot="prepend"></Icon>
            </Input>
        </FormItem>
        <FormItem prop="password">
            <Input @on-enter="handleSubmit('formInline')" type="password" v-model="formInline.password" placeholder="Password">
                <Icon type="ios-lock-outline" slot="prepend"></Icon>
            </Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="handleSubmit('formInline')">Sign in</Button>
        </FormItem>
    </Form>
    </div>
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

        handleSubmit(formName) {

            let username = this.formInline.user;
            let password = this.formInline.password;

            this.$refs[formName].validate((valid) => {
                if (valid) {
                    login(username, password)
                        .then(response => {
                            this.$router.push({ name: VIEW_NAME_ABOUT });
                        })
                        .catch(error => {
                            this.$Message.error('Authentication Failure');
                        })
                    ;
                } else {
                    this.$Message.error('Form fields are not valid');
                }
            });

        }


    }

}

</script>


<style scoped>

    .center-container {
        position: absolute;
        margin: 0;
        top: 50%;
        left: 50%;
        margin-right: -50%;
        transform: translate(-50%, -50%)
    }

</style>
