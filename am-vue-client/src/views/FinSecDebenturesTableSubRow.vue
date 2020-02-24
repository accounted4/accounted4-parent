<style scoped>
    .expand-row{
        margin-bottom: 16px;
    }
</style>


<template>

    <div>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Rate: </span>
                <span class="expand-value">{{ row.percentage }}%</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Issue date: </span>
                <span class="expand-value">{{ row.issueDte }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Underlying: </span>
                <span class="expand-value">{{ row.underlyingSymbol }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Close: </span>
                <span class="expand-value">{{ row.underlyingClosePrice }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Read Date: </span>
                <span class="expand-value">{{ row.underlyingReadDte }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Conversion Price: </span>
                <span class="expand-value">${{ row.conversionPrice }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Conversion Rate: </span>
                <span class="expand-value">{{ row.conversionRate }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Converted: </span>
                <span class="expand-value">${{ row.converted }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col>
                <span class="expand-key">Prospectus: </span>
                <a v-bind:href="row.prospectus" target="_blank">{{ row.prospectus }}</a>
            </Col>
        </Row>

        <Row>
            <Col>
                <p>{{ row.comments }}</p>
            </Col>
        </Row>

    </div>

</template>


<script>

    export default {

        props: {
            row: Object
        },

        data() {
            return {
                path: "Hello"
            }
        }

    };

</script>